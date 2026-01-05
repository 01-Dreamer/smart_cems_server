package zxylearn.smart_cems_server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import zxylearn.smart_cems_server.entity.AlertRecord;
import zxylearn.smart_cems_server.mapper.AlertRecordMapper;
import zxylearn.smart_cems_server.service.AlertRecordService;

@Service
public class AlertRecordServiceImpl extends ServiceImpl<AlertRecordMapper, AlertRecord> implements AlertRecordService {
}
