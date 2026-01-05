package zxylearn.smart_cems_server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import zxylearn.smart_cems_server.entity.EnergyData;

import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;

@Mapper
public interface EnergyDataMapper extends BaseMapper<EnergyData> {

    @Select("SELECT b.name as name, SUM(m_usage.daily_usage) as value " +
            "FROM ( " +
            "  SELECT meter_id, MAX(total_consumption) - MIN(total_consumption) as daily_usage " +
            "  FROM energy_data " +
            "  WHERE collect_time >= CURRENT_DATE " +
            "  GROUP BY meter_id " +
            ") as m_usage " +
            "JOIN meter m ON m_usage.meter_id = m.id " +
            "JOIN building b ON m.building_id = b.id " +
            "GROUP BY b.id, b.name")
    List<Map<String, Object>> getDailyConsumptionByBuilding();
}

