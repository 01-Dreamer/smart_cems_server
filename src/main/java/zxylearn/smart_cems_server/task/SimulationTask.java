package zxylearn.smart_cems_server.task;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import zxylearn.smart_cems_server.config.RabbitConfig;
import zxylearn.smart_cems_server.dto.EnergyDataMessage;
import zxylearn.smart_cems_server.entity.EnergyData;
import zxylearn.smart_cems_server.entity.Meter;
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
    private RabbitTemplate rabbitTemplate; // Pattern: Observer (via RabbitMQ)

    @Autowired
    private MeterService meterService;

    @Autowired
    private EnergyDataService energyDataService;

    @Autowired
    private SimulationDataFactory simulationDataFactory;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String REDIS_DATA_KEY = "energy:data:buffer";

    private final Random random = new Random();
    private int recordCount = 0;
    private final java.util.Map<Long, BigDecimal> meterConsumptionMap = new java.util.concurrent.ConcurrentHashMap<>();

    // 每5秒模拟生成一次能耗数据
    @Scheduled(fixedRate = 5000)
    public void simulateData() {
        List<Meter> meters = meterService.list();
        if (meters.isEmpty()) return;

        // 每20-50条记录触发一次故障
        recordCount++;
        boolean triggerFault = (recordCount % (20 + random.nextInt(31))) == 0;

        for (Meter meter : meters) {
            if (!"ONLINE".equals(meter.getStatus())) continue;

            // 使用工厂模式创建数据
            EnergyData data = simulationDataFactory.createEnergyData(meter, triggerFault);
            
            // 计算累计用电量
            calculateAccumulation(data, meter);

            // 写入 Redis 缓存
            redisTemplate.opsForList().rightPush(REDIS_DATA_KEY, data);
            String trendKey = "meter:trend:" + meter.getSn();
            redisTemplate.opsForList().leftPush(trendKey, data);
            redisTemplate.opsForList().trim(trendKey, 0, 49);

            // 发送消息到 RabbitMQ
            EnergyDataMessage message = new EnergyDataMessage(data, meter);
            rabbitTemplate.convertAndSend(RabbitConfig.ALERT_EXCHANGE, RabbitConfig.ALERT_ROUTING_KEY, message);
        }
    }

    // 每60秒将Redis缓存数据批量同步到MySQL
    @Scheduled(fixedRate = 60000)
    public void syncDataToDb() {
        List<EnergyData> batchList = new ArrayList<>();
        Long size = redisTemplate.opsForList().size(REDIS_DATA_KEY);
        
        if (size != null && size > 0) {
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

