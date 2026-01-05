package zxylearn.smart_cems_server.event;

import org.springframework.context.ApplicationEvent;
import zxylearn.smart_cems_server.entity.EnergyData;
import zxylearn.smart_cems_server.entity.Meter;

/**
 * 能耗数据采集事件 (观察者模式 - 事件对象)
 */
public class EnergyDataCollectedEvent extends ApplicationEvent {
    private final EnergyData energyData;

    private final Meter meter;

    public EnergyDataCollectedEvent(Object source, EnergyData energyData, Meter meter) {
        super(source);
        this.energyData = energyData;
        this.meter = meter;
    }

    public EnergyData getEnergyData() {
        return energyData;
    }

    public Meter getMeter() {
        return meter;
    }
}
