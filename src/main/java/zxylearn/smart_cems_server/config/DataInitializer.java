package zxylearn.smart_cems_server.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import zxylearn.smart_cems_server.entity.Building;
import zxylearn.smart_cems_server.entity.Meter;
import zxylearn.smart_cems_server.service.BuildingService;
import zxylearn.smart_cems_server.service.MeterService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final BuildingService buildingService;
    private final MeterService meterService;

    @Override
    public void run(String... args) throws Exception {
        if (buildingService.count() > 0) {
            System.out.println("已有初始化数据，跳过...");
            return;
        }

        System.out.println("开始初始化虚拟设备数据...");

        Building b1 = new Building();
        b1.setName("楸苑宿舍三号楼");
        b1.setLocationCode("QIU_3");
        b1.setTotalFloors(6);
        b1.setUsageType("DORMITORY");
        buildingService.save(b1);

        Building b2 = new Building();
        b2.setName("力行楼");
        b2.setLocationCode("LIXING");
        b2.setTotalFloors(5);
        b2.setUsageType("TEACHING");
        buildingService.save(b2);

        Building b3 = new Building();
        b3.setName("软件学院楼");
        b3.setLocationCode("SOFT_LAB");
        b3.setTotalFloors(10);
        b3.setUsageType("LAB");
        buildingService.save(b3);

        Building b4 = new Building();
        b4.setName("图书馆");
        b4.setLocationCode("LIBRARY");
        b4.setTotalFloors(8);
        b4.setUsageType("PUBLIC");
        buildingService.save(b4);

        List<Meter> meters = new ArrayList<>();

        meters.add(createMeter("宿舍智能电表-01", "METER_QIU_301", b1.getId(), "301", 1000));
        meters.add(createMeter("宿舍智能电表-02", "METER_QIU_302", b1.getId(), "302", 1000));
        meters.add(createMeter("宿舍智能电表-03", "METER_QIU_303", b1.getId(), "303", 1000));

        meters.add(createMeter("教室智能电表-01", "METER_LIXING_101", b2.getId(), "101", 3500));
        meters.add(createMeter("教室智能电表-02", "METER_LIXING_102", b2.getId(), "102", 3500));
        meters.add(createMeter("阶梯教室主控表", "METER_LIXING_205", b2.getId(), "205", 7000));

        meters.add(createMeter("实验室专用电表", "METER_SOFT_LAB1", b3.getId(), "306", 12000));
        meters.add(createMeter("办公室电表", "METER_SOFT_OFFICE", b3.getId(), "402", 4000));

        meters.add(createMeter("公共区域电表", "METER_LIB_HALL", b4.getId(), "一楼大厅", 6000));
        meters.add(createMeter("阅览室电表", "METER_LIB_READ", b4.getId(), "三楼阅览室", 3000));

        for (Meter m : meters) {
            try {
                meterService.addMeter(m);
            } catch (Exception e) {
                System.err.println("初始化设备失败: " + m.getName() + " - " + e.getMessage());
            }
        }
        
        System.out.println("数据初始化完成！");
    }

    private Meter createMeter(String name, String sn, Long buildingId, String roomNo, double ratedPower) {
        Meter m = new Meter();
        m.setName(name);
        m.setSn(sn);
        m.setBuildingId(buildingId);
        m.setRoomNo(roomNo);
        m.setRatedPower(BigDecimal.valueOf(ratedPower));
        m.setStatus("ONLINE");
        return m;
    }
}
