package zxylearn.smart_cems_server.factory;

import org.springframework.stereotype.Component;
import zxylearn.smart_cems_server.entity.EnergyData;
import zxylearn.smart_cems_server.entity.Meter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Random;

/**
 * 设计模式：工厂模式 (Factory Pattern)
 * 描述：封装创建模拟能耗数据对象的逻辑。
 */
@Component
public class SimulationDataFactory {

    private final Random random = new Random();

    public EnergyData createEnergyData(Meter meter, boolean triggerFault) {
        EnergyData data = new EnergyData();
        data.setMeterId(meter.getId());
        data.setCollectTime(LocalDateTime.now());

        // 电压模拟
        BigDecimal voltage;
        if (triggerFault && random.nextBoolean()) {
            // 故障：电压不稳定
            voltage = random.nextBoolean() ? new BigDecimal("180") : new BigDecimal("260");
        } else {
            // 正常：210V - 235V
            double v = 210 + random.nextDouble() * 25;
            voltage = BigDecimal.valueOf(v).setScale(2, RoundingMode.HALF_UP);
        }
        data.setVoltage(voltage);

        // 功率模拟
        BigDecimal power;
        LocalTime now = LocalTime.now();
        boolean isDay = now.isAfter(LocalTime.of(8, 0)) && now.isBefore(LocalTime.of(22, 0));

        if (triggerFault && !random.nextBoolean()) {
            // 故障：过载
            power = meter.getRatedPower().multiply(new BigDecimal("1.2"));
        } else {
            if (isDay) {
                // 日间模式：0.2 * 额定功率 到 0.9 * 额定功率
                double min = meter.getRatedPower().doubleValue() * 0.2;
                double max = meter.getRatedPower().doubleValue() * 0.9;
                double p = min + random.nextDouble() * (max - min);
                power = BigDecimal.valueOf(p).setScale(2, RoundingMode.HALF_UP);
            } else {
                // 夜间模式：10W - 100W
                double p = 10 + random.nextDouble() * 90;
                power = BigDecimal.valueOf(p).setScale(2, RoundingMode.HALF_UP);
            }
        }
        data.setPower(power);

        // 电流计算：I = P / U
        if (voltage.compareTo(BigDecimal.ZERO) != 0) {

            BigDecimal current = power.divide(voltage, 2, RoundingMode.HALF_UP);
            data.setCurrent(current);
        } else {
            data.setCurrent(BigDecimal.ZERO);
        }

        return data;
    }
}
