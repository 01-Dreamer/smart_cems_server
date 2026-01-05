package zxylearn.smart_cems_server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import zxylearn.smart_cems_server.entity.SysConfig;
import zxylearn.smart_cems_server.mapper.SysConfigMapper;
import zxylearn.smart_cems_server.service.SysConfigService;

@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    @Override
    public String getValue(String key) {
        SysConfig config = this.getById(key);
        return config != null ? config.getConfigValue() : null;
    }
}
