package zxylearn.smart_cems_server.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import zxylearn.smart_cems_server.entity.EnergyData;
import zxylearn.smart_cems_server.entity.Meter;
import zxylearn.smart_cems_server.event.EnergyDataCollectedEvent;
import zxylearn.smart_cems_server.factory.SimulationDataFactory;
import zxylearn.smart_cems_server.service.EnergyDataService;
import zxylearn.smart_cems_server.service.MeterService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SimulationTask {

    @Autowired
    private MeterService meterService;

    @Autowired
    private EnergyDataService energyDataService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private SimulationDataFactory simulationDataFactory;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String REDIS_DATA_KEY = "energy:data:buffer";

    private final Random random = new Random();
    private int recordCount = 0;
    private final java.util.Map<Long, BigDecimal> meterConsumptionMap = new java.util.concurrent.ConcurrentHashMap<>();

    @Scheduled(fixedRate = 5000) // 每5秒执行一次
    public void simulateData() {
        List<Meter> meters = meterService.list();
        if (meters.isEmpty()) return;

        recordCount++;
        boolean triggerFault = (recordCount % (20 + random.nextInt(31))) == 0; // 每20-50条记录触发一次故障

        for (Meter meter : meters) {
            if (!"ONLINE".equals(meter.getStatus())) continue;

            // 使用工厂模式创建数据
            EnergyData data = simulationDataFactory.createEnergyData(meter, triggerFault);
            
            // 计算累计用电量
            calculateAccumulation(data, meter);

            // 1. 写入 Redis 缓存 (List) - 用于持久化
            redisTemplate.opsForList().rightPush(REDIS_DATA_KEY, data);

            // 2. 写入 Redis 趋势缓存 (List) - 用于前端实时查询
            // 维护每个设备最近 50 条数据
            String trendKey = "meter:trend:" + meter.getSn();
            redisTemplate.opsForList().leftPush(trendKey, data);
            redisTemplate.opsForList().trim(trendKey, 0, 49);

            // 发布事件 (观察者模式) - 依然实时触发告警
            eventPublisher.publishEvent(new EnergyDataCollectedEvent(this, data, meter));
        }
    }

    @Scheduled(fixedRate = 60000) // 每1分钟同步一次到 MySQL
    public void syncDataToDb() {
        List<EnergyData> batchList = new ArrayList<>();
        Long size = redisTemplate.opsForList().size(REDIS_DATA_KEY);
        
        if (size != null && size > 0) {
            // 每次最多同步 1000 条，避免一次性压力过大
            int batchSize = 1000;
            long count = Math.min(size, batchSize);
            
            for (int i = 0; i < count; i++) {
                EnergyData data = (EnergyData) redisTemplate.opsForList().leftPop(REDIS_DATA_KEY);
                if (data != null) {
                    batchList.add(data);
                }
            }
            
            if (!batchList.isEmpty()) {
                energyDataService.saveBatch(batchList);
                log.info("已从 Redis 同步 {} 条记录到 MySQL。", batchList.size());
            }
        }
    }

    private void calculateAccumulation(EnergyData data, Meter meter) {
        BigDecimal powerKW = data.getPower().divide(new BigDecimal("1000"), 6, RoundingMode.HALF_UP);
        BigDecimal hours = new BigDecimal("5").divide(new BigDecimal("3600"), 6, RoundingMode.HALF_UP); // 5秒
        BigDecimal incremental = powerKW.multiply(hours);

        
        BigDecimal currentTotal = meterConsumptionMap.getOrDefault(meter.getId(), BigDecimal.ZERO);
        BigDecimal newTotal = currentTotal.add(incremental);
        meterConsumptionMap.put(meter.getId(), newTotal);
        
        data.setTotalConsumption(newTotal.setScale(4, RoundingMode.HALF_UP));
    }
}

