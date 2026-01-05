package zxylearn.smart_cems_server.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
    @Operation(summary = "获取告警记录 (分页, 支持按SN查询)")
    public Result<IPage<AlertRecord>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String sn) {
        Page<AlertRecord> pageParam = new Page<>(page, size);
        return Result.success(alertRecordService.listBySn(pageParam, sn));
    }



}
