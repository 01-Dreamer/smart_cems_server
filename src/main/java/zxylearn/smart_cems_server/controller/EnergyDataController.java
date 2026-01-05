package zxylearn.smart_cems_server.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import zxylearn.smart_cems_server.entity.EnergyData;
import zxylearn.smart_cems_server.service.EnergyDataService;

import zxylearn.smart_cems_server.common.Result;

@RestController
@RequestMapping("/energy")
@Tag(name = "能耗数据管理", description = "能耗数据查询接口")
public class EnergyDataController {

    @Autowired
    private EnergyDataService energyDataService;

    @GetMapping("/list")
    @Operation(summary = "获取能耗数据 (分页)")
    public Result<IPage<EnergyData>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<EnergyData> pageParam = new Page<>(page, size);
        return Result.success(energyDataService.page(pageParam, new LambdaQueryWrapper<EnergyData>()
                .orderByDesc(EnergyData::getCollectTime)));
    }
}


