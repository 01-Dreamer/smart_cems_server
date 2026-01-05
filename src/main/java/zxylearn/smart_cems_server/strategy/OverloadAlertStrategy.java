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
            alert.setDetails("当前功率 " + data.getPower() + "W 超过额定功率 " + meter.getRatedPower() + "W");
            alert.setTriggerTime(LocalDateTime.now());
            return Optional.of(alert);
        }
        return Optional.empty();
    }
}
