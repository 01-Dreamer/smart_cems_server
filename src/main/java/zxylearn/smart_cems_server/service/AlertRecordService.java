package zxylearn.smart_cems_server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import zxylearn.smart_cems_server.entity.AlertRecord;

import java.util.List;

public interface AlertRecordService extends IService<AlertRecord> {
    List<AlertRecord> listBySn(String sn);
}

