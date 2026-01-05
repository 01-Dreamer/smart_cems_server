package zxylearn.smart_cems_server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import zxylearn.smart_cems_server.entity.EnergyData;
import zxylearn.smart_cems_server.mapper.EnergyDataMapper;
import zxylearn.smart_cems_server.service.EnergyDataService;

@Service
public class EnergyDataServiceImpl extends ServiceImpl<EnergyDataMapper, EnergyData> implements EnergyDataService {
}
