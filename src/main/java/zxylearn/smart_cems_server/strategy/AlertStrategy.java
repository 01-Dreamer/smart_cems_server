package zxylearn.smart_cems_server.strategy;

import zxylearn.smart_cems_server.entity.AlertRecord;
import zxylearn.smart_cems_server.entity.EnergyData;
import zxylearn.smart_cems_server.entity.Meter;

import java.util.Optional;

// 告警策略
public interface AlertStrategy {
    Optional<AlertRecord> check(EnergyData data, Meter meter);
}

