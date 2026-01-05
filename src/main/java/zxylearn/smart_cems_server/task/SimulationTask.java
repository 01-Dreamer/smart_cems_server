package zxylearn.smart_cems_server.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import zxylearn.smart_cems_server.entity.EnergyData;
import zxylearn.smart_cems_server.entity.Meter;
import zxylearn.smart_cems_server.event.EnergyDataCollectedEvent;
import zxylearn.smart_cems_server.service.EnergyDataService;
import zxylearn.smart_cems_server.service.MeterService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    private final Random random = new Random();
    private int recordCount = 0;

    @Scheduled(fixedRate = 5000) // Run every 5 seconds
    public void simulateData() {
        List<Meter> meters = meterService.list();
        if (meters.isEmpty()) return;

        recordCount++;
        boolean triggerFault = (recordCount % (20 + random.nextInt(31))) == 0; // Trigger fault every 20-50 records

        for (Meter meter : meters) {
            if (!"ONLINE".equals(meter.getStatus())) continue;

            EnergyData data = generateData(meter, triggerFault);
            energyDataService.save(data);

            // Publish event for Observer Pattern
            eventPublisher.publishEvent(new EnergyDataCollectedEvent(this, data, meter));
        }
    }

    private final java.util.Map<Long, BigDecimal> meterConsumptionMap = new java.util.concurrent.ConcurrentHashMap<>();

    private EnergyData generateData(Meter meter, boolean triggerFault) {
        EnergyData data = new EnergyData();
        data.setMeterId(meter.getId());
        data.setCollectTime(LocalDateTime.now());

        // Voltage Simulation
        BigDecimal voltage;
        if (triggerFault && random.nextBoolean()) {
            // Fault: Voltage instability
            voltage = random.nextBoolean() ? new BigDecimal("180") : new BigDecimal("260");
        } else {
            // Normal: 210V - 235V
            double v = 210 + random.nextDouble() * 25;
            voltage = BigDecimal.valueOf(v).setScale(2, RoundingMode.HALF_UP);
        }
        data.setVoltage(voltage);

        // Power Simulation
        BigDecimal power;
        LocalTime now = LocalTime.now();
        boolean isDay = now.isAfter(LocalTime.of(8, 0)) && now.isBefore(LocalTime.of(22, 0));

        if (triggerFault && !random.nextBoolean()) {
             // Fault: Overload
             power = meter.getRatedPower().multiply(new BigDecimal("1.2"));
        } else {
            if (isDay) {
                // Day: 0.2 * Rated to 0.9 * Rated
                double min = meter.getRatedPower().doubleValue() * 0.2;
                double max = meter.getRatedPower().doubleValue() * 0.9;
                double p = min + random.nextDouble() * (max - min);
                power = BigDecimal.valueOf(p).setScale(2, RoundingMode.HALF_UP);
            } else {
                // Night: 10W - 100W
                double p = 10 + random.nextDouble() * 90;
                power = BigDecimal.valueOf(p).setScale(2, RoundingMode.HALF_UP);
            }
        }
        data.setPower(power);

        // Current Calculation: I = P / U
        if (voltage.compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal current = power.divide(voltage, 2, RoundingMode.HALF_UP);
            data.setCurrent(current);
        } else {
            data.setCurrent(BigDecimal.ZERO);
        }

        // Total Consumption
        BigDecimal powerKW = power.divide(new BigDecimal("1000"), 6, RoundingMode.HALF_UP);
        BigDecimal hours = new BigDecimal("5").divide(new BigDecimal("3600"), 6, RoundingMode.HALF_UP);
        BigDecimal incremental = powerKW.multiply(hours);
        
        BigDecimal currentTotal = meterConsumptionMap.getOrDefault(meter.getId(), BigDecimal.ZERO);
        BigDecimal newTotal = currentTotal.add(incremental);
        meterConsumptionMap.put(meter.getId(), newTotal);
        
        data.setTotalConsumption(newTotal.setScale(4, RoundingMode.HALF_UP));

        return data;
    }
}
