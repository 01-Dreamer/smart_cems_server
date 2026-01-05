package zxylearn.smart_cems_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import zxylearn.smart_cems_server.entity.AlertRecord;
import zxylearn.smart_cems_server.entity.Meter;
import zxylearn.smart_cems_server.mapper.AlertRecordMapper;
import zxylearn.smart_cems_server.mapper.MeterMapper;
import zxylearn.smart_cems_server.service.AlertRecordService;

@Service
@RequiredArgsConstructor
public class AlertRecordServiceImpl extends ServiceImpl<AlertRecordMapper, AlertRecord> implements AlertRecordService {

    private final MeterMapper meterMapper;

    @Override
    public IPage<AlertRecord> listBySn(Page<AlertRecord> page, String sn) {
        LambdaQueryWrapper<AlertRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(AlertRecord::getTriggerTime);

        if (sn != null && !sn.isEmpty()) {
            Meter meter = meterMapper.selectOne(new LambdaQueryWrapper<Meter>().eq(Meter::getSn, sn));
            if (meter != null) {
                queryWrapper.eq(AlertRecord::getMeterId, meter.getId());
            } else {
                // If SN provided but meter not found, return empty page
                return page;
            }
        }
        
        return this.page(page, queryWrapper);
    }
}


