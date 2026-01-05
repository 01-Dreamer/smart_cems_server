package zxylearn.smart_cems_server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import zxylearn.smart_cems_server.entity.Building;
import zxylearn.smart_cems_server.service.BuildingService;

import zxylearn.smart_cems_server.common.Result;

import java.util.List;

@RestController
@RequestMapping("/building")
@Tag(name = "建筑管理", description = "建筑信息管理接口")
public class BuildingController {

    @Autowired
    private BuildingService buildingService;

    @GetMapping("/list")
    @Operation(summary = "获取所有建筑列表")
    public Result<List<Building>> list() {
        return Result.success(buildingService.list());
    }

    @PostMapping("/add")
    @Operation(summary = "添加建筑")
    public Result<Boolean> add(@RequestBody Building building) {
        return Result.success(buildingService.save(building));
    }
}

