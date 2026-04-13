package com.campus.safety.module.warning.controller;

import com.campus.safety.common.annotation.Log;
import com.campus.safety.common.result.R;
import com.campus.safety.common.utils.PageResult;
import com.campus.safety.module.warning.dto.WarningHandleDTO;
import com.campus.safety.module.warning.dto.WarningQueryDTO;
import com.campus.safety.module.warning.dto.WarningRecordDTO;
import com.campus.safety.module.warning.entity.WarningRecord;
import com.campus.safety.module.warning.service.WarningRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 预警记录控制器
 */
@RestController
@RequestMapping("/warning/record")
@RequiredArgsConstructor
public class WarningRecordController {

    private final WarningRecordService warningRecordService;

    /**
     * 分页查询预警记录
     */
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('warning:record:list')")
    @Log(module = "预警记录管理", description = "分页查询预警记录")
    public R<PageResult<WarningRecord>> page(WarningQueryDTO queryDTO) {
        return R.ok(warningRecordService.queryPage(queryDTO));
    }

    /**
     * 获取预警记录详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('warning:record:list')")
    @Log(module = "预警记录管理", description = "查询预警记录详情")
    public R<WarningRecord> getById(@PathVariable Long id) {
        return R.ok(warningRecordService.getById(id));
    }

    /**
     * 新增预警记录
     */
    @PostMapping
    @PreAuthorize("hasAuthority('warning:record:add')")
    @Log(module = "预警记录管理", description = "新增预警记录")
    public R<Void> add(@RequestBody @Valid WarningRecordDTO dto) {
        warningRecordService.add(dto);
        return R.ok();
    }

    /**
     * 编辑预警记录
     */
    @PutMapping
    @PreAuthorize("hasAuthority('warning:record:edit')")
    @Log(module = "预警记录管理", description = "编辑预警记录")
    public R<Void> update(@RequestBody @Valid WarningRecordDTO dto) {
        warningRecordService.update(dto);
        return R.ok();
    }

    /**
     * 删除预警记录
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('warning:record:delete')")
    @Log(module = "预警记录管理", description = "删除预警记录")
    public R<Void> delete(@PathVariable Long id) {
        warningRecordService.delete(id);
        return R.ok();
    }

    /**
     * 处理预警
     */
    @PutMapping("/handle")
    @PreAuthorize("hasAuthority('warning:record:handle')")
    @Log(module = "预警记录管理", description = "处理预警")
    public R<Void> handle(@RequestBody @Valid WarningHandleDTO dto) {
        warningRecordService.handle(dto);
        return R.ok();
    }

    /**
     * 按类型统计
     */
    @GetMapping("/stats/type")
    @PreAuthorize("hasAuthority('warning:record:list')")
    @Log(module = "预警记录管理", description = "按类型统计预警")
    public R<Map<String, Long>> statsByType() {
        return R.ok(warningRecordService.statsByType());
    }

    /**
     * 按级别统计
     */
    @GetMapping("/stats/level")
    @PreAuthorize("hasAuthority('warning:record:list')")
    @Log(module = "预警记录管理", description = "按级别统计预警")
    public R<Map<String, Long>> statsByLevel() {
        return R.ok(warningRecordService.statsByLevel());
    }

    /**
     * 未处理数量
     */
    @GetMapping("/unhandled/count")
    @PreAuthorize("hasAuthority('warning:record:list')")
    @Log(module = "预警记录管理", description = "查询未处理预警数量")
    public R<Long> unhandledCount() {
        return R.ok(warningRecordService.unhandledCount());
    }
}
