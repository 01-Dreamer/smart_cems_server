package zxylearn.smart_cems_server.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import zxylearn.smart_cems_server.entity.EnergyData;
import zxylearn.smart_cems_server.entity.Meter;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnergyDataMessage implements Serializable {
    private EnergyData energyData;
    private Meter meter;
}
