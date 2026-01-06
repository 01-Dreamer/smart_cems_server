package zxylearn.smart_cems_server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import zxylearn.smart_cems_server.entity.Meter;
import zxylearn.smart_cems_server.service.MeterService;

import zxylearn.smart_cems_server.common.Result;

import java.util.List;

@RestController
@RequestMapping("/meter")
@Tag(name = "设备管理")
public class MeterController {

    @Autowired
    private MeterService meterService;

    @GetMapping("/list")
    @Operation(summary = "获取所有设备列表")
    public Result<List<Meter>> list() {
        return Result.success(meterService.list());
    }

    @PostMapping("/add")
    @Operation(summary = "添加设备")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> add(@RequestBody Meter meter) {
        return Result.success(meterService.addMeter(meter));
    }
}

