package zxylearn.smart_cems_server.service;

import java.util.List;
import java.util.Map;
import zxylearn.smart_cems_server.entity.EnergyData;

public interface StatsService {
    List<EnergyData> getMeterTrend(String sn);
    List<Map<String, Object>> getBuildingStats();
}
