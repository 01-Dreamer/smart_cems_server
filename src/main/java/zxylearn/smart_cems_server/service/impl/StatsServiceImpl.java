package zxylearn.smart_cems_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import zxylearn.smart_cems_server.entity.EnergyData;
import zxylearn.smart_cems_server.entity.Meter;
import zxylearn.smart_cems_server.mapper.EnergyDataMapper;
import zxylearn.smart_cems_server.mapper.MeterMapper;
import zxylearn.smart_cems_server.service.StatsService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final EnergyDataMapper energyDataMapper;
    private final MeterMapper meterMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public List<EnergyData> getMeterTrend(String sn, Integer limit) {
        // 1. 尝试从 Redis 获取趋势缓存
        String trendKey = "meter:trend:" + sn;
        List<Object> cachedData = redisTemplate.opsForList().range(trendKey, 0, limit - 1);

        if (cachedData != null && !cachedData.isEmpty()) {
            try {
                return cachedData.stream()
                        .map(obj -> (EnergyData) obj)
                        .collect(Collectors.toList());
            } catch (Exception e) {
                // 如果转换失败（例如 Redis 中数据格式不兼容），降级查数据库
                e.printStackTrace();
            }
        }

        // 2. Redis 未命中或异常，查询数据库 (兜底)
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
