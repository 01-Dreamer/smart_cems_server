package zxylearn.smart_cems_server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import zxylearn.smart_cems_server.entity.Building;
import zxylearn.smart_cems_server.mapper.BuildingMapper;
import zxylearn.smart_cems_server.service.BuildingService;

import java.util.List;

@Service
public class BuildingServiceImpl extends ServiceImpl<BuildingMapper, Building> implements BuildingService {

    @Override
    @Cacheable(value = "buildings", key = "'all'")
    public List<Building> list() {
        return super.list();
    }

    @Override
    @CacheEvict(value = "buildings", allEntries = true)
    public boolean save(Building entity) {
        return super.save(entity);
    }
}
