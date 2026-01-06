package zxylearn.smart_cems_server.event;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import zxylearn.smart_cems_server.config.RabbitConfig;
import zxylearn.smart_cems_server.dto.EnergyDataMessage;
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

    // 监听 RabbitMQ 队列
    @RabbitListener(queues = RabbitConfig.ALERT_QUEUE)
    public void handleEnergyDataMessage(EnergyDataMessage message) {

        for (AlertStrategy strategy : alertStrategies) {
            Optional<AlertRecord> alert = strategy.check(message.getEnergyData(), message.getMeter());
            alert.ifPresent(alertRecordService::save);
        }
    }
}
