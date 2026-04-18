package com.campus.safety.module.visitor.controller;

import com.campus.safety.common.annotation.Log;
import com.campus.safety.common.result.R;
import com.campus.safety.common.utils.PageResult;
import com.campus.safety.module.visitor.dto.VisitorAuditDTO;
import com.campus.safety.module.visitor.dto.VisitorDTO;
import com.campus.safety.module.visitor.dto.VisitorQueryDTO;
import com.campus.safety.module.visitor.entity.VisitorRecord;
import com.campus.safety.module.visitor.service.VisitorRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 访客记录控制器
 */
@RestController
@RequestMapping("/visitor")
@RequiredArgsConstructor
public class VisitorRecordController {

    private final VisitorRecordService visitorRecordService;

    /**
     * 分页查询访客记录
     */
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('visitor:record:list')")
    @Log(module = "访客管理", description = "分页查询访客记录")
    public R<PageResult<VisitorRecord>> page(VisitorQueryDTO queryDTO) {
        return R.ok(visitorRecordService.queryPage(queryDTO));
    }

    /**
     * 获取访客记录详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('visitor:record:list')")
    @Log(module = "访客管理", description = "查询访客详情")
    public R<VisitorRecord> getById(@PathVariable Long id) {
        return R.ok(visitorRecordService.getById(id));
    }

    /**
     * 新增访客记录
     */
    @PostMapping
    @PreAuthorize("hasAuthority('visitor:record:add')")
    @Log(module = "访客管理", description = "新增访客记录")
    public R<Void> add(@RequestBody @Valid VisitorDTO dto) {
        visitorRecordService.add(dto);
        return R.ok();
    }

    /**
     * 编辑访客记录
     */
    @PutMapping
    @PreAuthorize("hasAuthority('visitor:record:edit')")
    @Log(module = "访客管理", description = "编辑访客记录")
    public R<Void> update(@RequestBody @Valid VisitorDTO dto) {
        visitorRecordService.update(dto);
        return R.ok();
    }

    /**
     * 删除访客记录
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('visitor:record:delete')")
    @Log(module = "访客管理", description = "删除访客记录")
    public R<Void> delete(@PathVariable Long id) {
        visitorRecordService.delete(id);
        return R.ok();
    }

    /**
     * 审核访客
     */
    @PutMapping("/audit")
    @PreAuthorize("hasAuthority('visitor:record:audit')")
    @Log(module = "访客管理", description = "审核访客记录")
    public R<Void> audit(@RequestBody @Valid VisitorAuditDTO dto) {
        visitorRecordService.audit(dto);
        return R.ok();
    }

    /**
     * 入校签到
     */
    @PutMapping("/check-in/{id}")
    @PreAuthorize("hasAuthority('visitor:checkin')")
    @Log(module = "访客管理", description = "访客入校签到")
    public R<Void> checkIn(@PathVariable Long id) {
        visitorRecordService.checkIn(id);
        return R.ok();
    }

    /**
     * 离校签退
     */
    @PutMapping("/check-out/{id}")
    @PreAuthorize("hasAuthority('visitor:checkout')")
    @Log(module = "访客管理", description = "访客离校签退")
    public R<Void> checkOut(@PathVariable Long id) {
        visitorRecordService.checkOut(id);
        return R.ok();
    }

    /**
     * 今日访客统计
     */
    @GetMapping("/today-stats")
    @PreAuthorize("hasAuthority('visitor:record:list')")
    @Log(module = "访客管理", description = "查看今日访客统计")
    public R<Map<String, Object>> todayStats() {
        return R.ok(visitorRecordService.todayStats());
    }
}
