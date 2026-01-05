package zxylearn.smart_cems_server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import zxylearn.smart_cems_server.entity.AlertRecord;

public interface AlertRecordService extends IService<AlertRecord> {
    IPage<AlertRecord> listBySn(Page<AlertRecord> page, String sn);
}


