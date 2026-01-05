package zxylearn.smart_cems_server.strategy;

import org.springframework.stereotype.Component;
import zxylearn.smart_cems_server.entity.AlertRecord;
import zxylearn.smart_cems_server.entity.EnergyData;
import zxylearn.smart_cems_server.entity.Meter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class VoltageAlertStrategy implements AlertStrategy {
    @Override
    public Optional<AlertRecord> check(EnergyData data, Meter meter) {
        BigDecimal voltage = data.getVoltage();
        // 220V +/- 10% => 198V - 242V
        if (voltage.compareTo(new BigDecimal("198")) < 0 || voltage.compareTo(new BigDecimal("242")) > 0) {
            AlertRecord alert = new AlertRecord();
            alert.setMeterId(meter.getId());
            alert.setAlertType("VOLTAGE_ABNORMAL");
            alert.setAlertValue(voltage);
            alert.setDetails("Voltage " + voltage + "V is out of normal range (198V-242V)");
            alert.setTriggerTime(LocalDateTime.now());
            return Optional.of(alert);
        }
        return Optional.empty();
    }
}
