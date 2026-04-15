package com.campus.safety.module.report.controller;

import com.campus.safety.common.annotation.Log;
import com.campus.safety.common.result.R;
import com.campus.safety.module.report.service.ReportService;
import com.campus.safety.module.report.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 报表统计控制器
 */
@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * 获取仪表盘数据
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('report:dashboard:view')")
    @Log(module = "报表统计", description = "查看仪表盘")
    public R<DashboardVO> dashboard() {
        return R.ok(reportService.getDashboard());
    }

    /**
     * 获取访客统计
     */
    @GetMapping("/visitor-stats")
    @PreAuthorize("hasAuthority('report:dashboard:view')")
    @Log(module = "报表统计", description = "查看访客统计")
    public R<VisitorStatsVO> visitorStats(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return R.ok(reportService.getVisitorStats(startDate, endDate));
    }

    /**
     * 获取设备统计
     */
    @GetMapping("/device-stats")
    @PreAuthorize("hasAuthority('report:dashboard:view')")
    @Log(module = "报表统计", description = "查看设备统计")
    public R<DeviceStatsVO> deviceStats() {
        return R.ok(reportService.getDeviceStats());
    }

    /**
     * 获取预警统计
     */
    @GetMapping("/warning-stats")
    @PreAuthorize("hasAuthority('report:dashboard:view')")
    @Log(module = "报表统计", description = "查看预警统计")
    public R<WarningStatsVO> warningStats(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return R.ok(reportService.getWarningStats(startDate, endDate));
    }

    /**
     * 获取资产统计
     */
    @GetMapping("/asset-stats")
    @PreAuthorize("hasAuthority('report:dashboard:view')")
    @Log(module = "报表统计", description = "查看资产统计")
    public R<AssetStatsVO> assetStats() {
        return R.ok(reportService.getAssetStats());
    }
}
