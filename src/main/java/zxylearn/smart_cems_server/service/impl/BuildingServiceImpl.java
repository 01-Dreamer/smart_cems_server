package zxylearn.smart_cems_server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import zxylearn.smart_cems_server.entity.Building;
import zxylearn.smart_cems_server.mapper.BuildingMapper;
import zxylearn.smart_cems_server.service.BuildingService;

@Service
public class BuildingServiceImpl extends ServiceImpl<BuildingMapper, Building> implements BuildingService {
}
