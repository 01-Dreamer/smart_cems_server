package zxylearn.smart_cems_server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("energy_data")
public class EnergyData implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long meterId;
    private BigDecimal voltage;
    private BigDecimal current;
    private BigDecimal power;
    private BigDecimal totalConsumption;
    private LocalDateTime collectTime;
}
