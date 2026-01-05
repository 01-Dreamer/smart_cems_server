package zxylearn.smart_cems_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import zxylearn.smart_cems_server.entity.EnergyData;
import zxylearn.smart_cems_server.entity.Meter;
import zxylearn.smart_cems_server.mapper.EnergyDataMapper;
import zxylearn.smart_cems_server.mapper.MeterMapper;
import zxylearn.smart_cems_server.service.StatsService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final EnergyDataMapper energyDataMapper;
    private final MeterMapper meterMapper;

    @Override
    public List<EnergyData> getMeterTrend(String sn, Integer limit) {
        Meter meter = meterMapper.selectOne(new LambdaQueryWrapper<Meter>().eq(Meter::getSn, sn));
        if (meter == null) {
            return Collections.emptyList();
        }
        return energyDataMapper.selectList(new LambdaQueryWrapper<EnergyData>()
                .eq(EnergyData::getMeterId, meter.getId())
                .orderByDesc(EnergyData::getCollectTime)
                .last("LIMIT " + limit));
    }

    @Override
    public List<Map<String, Object>> getBuildingStats() {
        return energyDataMapper.getDailyConsumptionByBuilding();
    }
}
