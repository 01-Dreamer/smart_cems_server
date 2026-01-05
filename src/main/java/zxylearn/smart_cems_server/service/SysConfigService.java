package zxylearn.smart_cems_server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import zxylearn.smart_cems_server.entity.SysConfig;

public interface SysConfigService extends IService<SysConfig> {
    String getValue(String key);
}
