package zxylearn.smart_cems_server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import zxylearn.smart_cems_server.common.Result;
import zxylearn.smart_cems_server.entity.AlertRecord;

import zxylearn.smart_cems_server.service.AlertRecordService;

import java.util.List;

@RestController
@RequestMapping("/alert")
@Tag(name = "告警管理", description = "异常告警查询接口")
public class AlertController {

    @Autowired
    private AlertRecordService alertRecordService;

    @GetMapping("/list")
    @Operation(summary = "获取告警记录 (支持按SN查询)")
    public Result<List<AlertRecord>> list(@RequestParam(required = false) String sn) {
        return Result.success(alertRecordService.listBySn(sn));
    }


}
