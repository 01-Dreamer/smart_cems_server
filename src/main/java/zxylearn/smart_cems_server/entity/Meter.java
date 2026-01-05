package zxylearn.smart_cems_server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("meter")
public class Meter implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String sn;
    private String status; // ONLINE, OFFLINE, REMOVED
    private BigDecimal ratedPower;
    private Long buildingId;
    private String roomNo;
    private LocalDateTime createTime;
}
