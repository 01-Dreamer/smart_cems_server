package zxylearn.smart_cems_server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import zxylearn.smart_cems_server.entity.EnergyData;
import zxylearn.smart_cems_server.service.EnergyDataService;

import zxylearn.smart_cems_server.common.Result;

import java.util.List;

@RestController
@RequestMapping("/energy")
@Tag(name = "能耗数据管理", description = "能耗数据查询接口")
public class EnergyDataController {

    @Autowired
    private EnergyDataService energyDataService;

    @GetMapping("/list")
    @Operation(summary = "获取所有能耗数据")
    public Result<List<EnergyData>> list() {
        return Result.success(energyDataService.list());
    }
}

