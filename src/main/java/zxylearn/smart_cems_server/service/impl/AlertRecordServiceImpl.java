package zxylearn.smart_cems_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import zxylearn.smart_cems_server.entity.AlertRecord;
import zxylearn.smart_cems_server.entity.Meter;
import zxylearn.smart_cems_server.mapper.AlertRecordMapper;
import zxylearn.smart_cems_server.mapper.MeterMapper;
import zxylearn.smart_cems_server.service.AlertRecordService;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertRecordServiceImpl extends ServiceImpl<AlertRecordMapper, AlertRecord> implements AlertRecordService {

    private final MeterMapper meterMapper;

    @Override
    public List<AlertRecord> listBySn(String sn) {
        if (sn == null || sn.isEmpty()) {
            return this.list();
        }
        Meter meter = meterMapper.selectOne(new LambdaQueryWrapper<Meter>().eq(Meter::getSn, sn));
        if (meter == null) {
            return Collections.emptyList();
        }
        return this.list(new LambdaQueryWrapper<AlertRecord>()
                .eq(AlertRecord::getMeterId, meter.getId())
                .orderByDesc(AlertRecord::getTriggerTime));
    }
}

