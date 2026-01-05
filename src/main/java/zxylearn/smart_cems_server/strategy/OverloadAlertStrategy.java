package zxylearn.smart_cems_server.strategy;

import org.springframework.stereotype.Component;
import zxylearn.smart_cems_server.entity.AlertRecord;
import zxylearn.smart_cems_server.entity.EnergyData;
import zxylearn.smart_cems_server.entity.Meter;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class OverloadAlertStrategy implements AlertStrategy {
    @Override
    public Optional<AlertRecord> check(EnergyData data, Meter meter) {
        if (data.getPower().compareTo(meter.getRatedPower()) > 0) {
            AlertRecord alert = new AlertRecord();
            alert.setMeterId(meter.getId());
            alert.setAlertType("OVERLOAD");
            alert.setAlertValue(data.getPower());
            alert.setDetails("Current power " + data.getPower() + "W exceeds rated power " + meter.getRatedPower() + "W");
            alert.setTriggerTime(LocalDateTime.now());
            return Optional.of(alert);
        }
        return Optional.empty();
    }
}
