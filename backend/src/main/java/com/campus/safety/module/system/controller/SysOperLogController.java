package com.campus.safety.module.system.controller;

import com.campus.safety.common.annotation.Log;
import com.campus.safety.common.result.R;
import com.campus.safety.common.utils.PageResult;
import com.campus.safety.module.system.dto.SysOperLogQueryDTO;
import com.campus.safety.module.system.entity.SysOperLog;
import com.campus.safety.module.system.service.SysOperLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 操作日志控制器
 */
@RestController
@RequestMapping("/system/log")
@RequiredArgsConstructor
public class SysOperLogController {

    private final SysOperLogService sysOperLogService;

    /**
     * 分页查询操作日志
     */
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('system:log:list')")
    public R<PageResult<SysOperLog>> page(SysOperLogQueryDTO queryDTO) {
        return R.ok(sysOperLogService.queryPage(queryDTO));
    }

    /**
     * 清空操作日志
     */
    @DeleteMapping("/clear")
    @PreAuthorize("hasAuthority('system:log:delete')")
    @Log(module = "日志管理", description = "清空操作日志")
    public R<Void> clear() {
        sysOperLogService.clear();
        return R.ok();
    }
}
