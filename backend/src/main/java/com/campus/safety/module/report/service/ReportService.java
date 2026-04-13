package com.campus.safety.module.report.service;

import com.campus.safety.module.report.vo.*;

/**
 * 报表统计服务
 */
public interface ReportService {

    /**
     * 获取仪表盘数据
     */
    DashboardVO getDashboard();

    /**
     * 获取访客统计数据
     * @param startDate 开始日期 (yyyy-MM-dd)
     * @param endDate 结束日期 (yyyy-MM-dd)
     */
    VisitorStatsVO getVisitorStats(String startDate, String endDate);

    /**
     * 获取设备统计数据
     */
    DeviceStatsVO getDeviceStats();

    /**
     * 获取预警统计数据
     * @param startDate 开始日期 (yyyy-MM-dd)
     * @param endDate 结束日期 (yyyy-MM-dd)
     */
    WarningStatsVO getWarningStats(String startDate, String endDate);

    /**
     * 获取资产统计数据
     */
    AssetStatsVO getAssetStats();
}
