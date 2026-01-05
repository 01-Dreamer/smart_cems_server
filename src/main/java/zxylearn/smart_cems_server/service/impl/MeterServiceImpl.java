package zxylearn.smart_cems_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import zxylearn.smart_cems_server.entity.Meter;
import zxylearn.smart_cems_server.mapper.MeterMapper;
import zxylearn.smart_cems_server.service.MeterService;

@Service
public class MeterServiceImpl extends ServiceImpl<MeterMapper, Meter> implements MeterService {

    @Override
    public boolean addMeter(Meter meter) {
        // 约束：同一个房间只能有一个有效设备（非拆除状态）
        if (!"REMOVED".equals(meter.getStatus())) {
            Long count = this.count(new LambdaQueryWrapper<Meter>()
                    .eq(Meter::getBuildingId, meter.getBuildingId())
                    .eq(Meter::getRoomNo, meter.getRoomNo())
                    .ne(Meter::getStatus, "REMOVED"));
            if (count > 0) {
                throw new RuntimeException("该房间已存在有效设备！");
            }
        }
        return this.save(meter);
    }
}
