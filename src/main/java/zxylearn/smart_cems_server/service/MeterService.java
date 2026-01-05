package zxylearn.smart_cems_server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import zxylearn.smart_cems_server.entity.Meter;

public interface MeterService extends IService<Meter> {
    boolean addMeter(Meter meter);
}
