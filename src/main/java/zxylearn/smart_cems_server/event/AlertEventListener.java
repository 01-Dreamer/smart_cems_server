package zxylearn.smart_cems_server.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import zxylearn.smart_cems_server.entity.AlertRecord;
import zxylearn.smart_cems_server.service.AlertRecordService;
import zxylearn.smart_cems_server.strategy.AlertStrategy;

import java.util.List;
import java.util.Optional;

@Component
public class AlertEventListener {

    @Autowired
    private List<AlertStrategy> alertStrategies;

    @Autowired
    private AlertRecordService alertRecordService;

    // 设计模式：观察者模式 (Observer Pattern)
    @EventListener
    @Async // 可选：异步处理告警
    public void handleEnergyDataCollected(EnergyDataCollectedEvent event) {

        for (AlertStrategy strategy : alertStrategies) {
            Optional<AlertRecord> alert = strategy.check(event.getEnergyData(), event.getMeter());
            alert.ifPresent(alertRecordService::save);
        }
    }
}
