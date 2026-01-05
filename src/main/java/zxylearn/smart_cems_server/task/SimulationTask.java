package zxylearn.smart_cems_server.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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
import java.util.List;
import java.util.Random;

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

            energyDataService.save(data);

            // 发布事件 (观察者模式)
            eventPublisher.publishEvent(new EnergyDataCollectedEvent(this, data, meter));
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

