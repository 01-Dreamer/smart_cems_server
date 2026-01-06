package zxylearn.smart_cems_server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("alert_record")
public class AlertRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long meterId;
    private String alertType; // OVERLOAD -- 功率过大, VOLTAGE_ABNORMAL -- 电压异常
    private BigDecimal alertValue;
    private String details;
    private LocalDateTime triggerTime;
}
