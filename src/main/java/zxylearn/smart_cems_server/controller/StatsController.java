package zxylearn.smart_cems_server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import zxylearn.smart_cems_server.common.Result;
import zxylearn.smart_cems_server.entity.EnergyData;
import zxylearn.smart_cems_server.service.StatsService;

import java.util.List;
import java.util.Map;

@Tag(name = "统计分析接口")
@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @Operation(summary = "获取设备历史趋势 (最近10条)")
    @GetMapping("/trend")
    public Result<List<EnergyData>> getMeterTrend(@RequestParam String sn) {
        return Result.success(statsService.getMeterTrend(sn));
    }

    @Operation(summary = "获取建筑今日用电占比")
    @GetMapping("/building-share")
    public Result<List<Map<String, Object>>> getBuildingStats() {
        return Result.success(statsService.getBuildingStats());
    }
}
