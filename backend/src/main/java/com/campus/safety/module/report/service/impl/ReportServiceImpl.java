package com.campus.safety.module.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.safety.module.asset.entity.AssetInfo;
import com.campus.safety.module.asset.entity.DeviceInfo;
import com.campus.safety.module.asset.mapper.AssetInfoMapper;
import com.campus.safety.module.asset.mapper.DeviceInfoMapper;
import com.campus.safety.module.report.service.ReportService;
import com.campus.safety.module.report.vo.*;
import com.campus.safety.module.visitor.entity.VisitorRecord;
import com.campus.safety.module.visitor.mapper.VisitorRecordMapper;
import com.campus.safety.module.warning.entity.WarningRecord;
import com.campus.safety.module.warning.mapper.WarningRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 报表统计服务实现
 */
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final VisitorRecordMapper visitorRecordMapper;
    private final DeviceInfoMapper deviceInfoMapper;
    private final AssetInfoMapper assetInfoMapper;
    private final WarningRecordMapper warningRecordMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Override
    public DashboardVO getDashboard() {
        DashboardVO dashboard = new DashboardVO();

        // 今日时间范围
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);

        // 今日访客数
        Long todayVisitorCount = visitorRecordMapper.selectCount(
                new LambdaQueryWrapper<VisitorRecord>()
                        .ge(VisitorRecord::getCreateTime, todayStart)
                        .le(VisitorRecord::getCreateTime, todayEnd)
        );
        dashboard.setTodayVisitorCount(todayVisitorCount.intValue());

        // 设备总数
        Long totalDeviceCount = deviceInfoMapper.selectCount(
                new LambdaQueryWrapper<DeviceInfo>()
        );
        dashboard.setTotalDeviceCount(totalDeviceCount.intValue());

        // 在线设备数
        Long onlineDeviceCount = deviceInfoMapper.selectCount(
                new LambdaQueryWrapper<DeviceInfo>()
                        .eq(DeviceInfo::getOnline, 1)
        );
        dashboard.setOnlineDeviceCount(onlineDeviceCount.intValue());

        // 未处理预警数
        Long unhandledWarningCount = warningRecordMapper.selectCount(
                new LambdaQueryWrapper<WarningRecord>()
                        .eq(WarningRecord::getStatus, 0)
        );
        dashboard.setUnhandledWarningCount(unhandledWarningCount.intValue());

        // 资产总数
        Long totalAssetCount = assetInfoMapper.selectCount(
                new LambdaQueryWrapper<AssetInfo>()
        );
        dashboard.setTotalAssetCount(totalAssetCount.intValue());

        // 今日预警数
        Long todayWarningCount = warningRecordMapper.selectCount(
                new LambdaQueryWrapper<WarningRecord>()
                        .ge(WarningRecord::getCreateTime, todayStart)
                        .le(WarningRecord::getCreateTime, todayEnd)
        );
        dashboard.setTodayWarningCount(todayWarningCount.intValue());

        // 最近7天访客趋势
        List<DashboardVO.TrendItem> visitorTrend = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.atTime(LocalTime.MAX);
            Long count = visitorRecordMapper.selectCount(
                    new LambdaQueryWrapper<VisitorRecord>()
                            .ge(VisitorRecord::getCreateTime, dayStart)
                            .le(VisitorRecord::getCreateTime, dayEnd)
            );
            visitorTrend.add(new DashboardVO.TrendItem(date.format(DATE_FORMATTER), count.intValue()));
        }
        dashboard.setVisitorTrend(visitorTrend);

        // 最近7天预警趋势
        List<DashboardVO.TrendItem> warningTrend = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.atTime(LocalTime.MAX);
            Long count = warningRecordMapper.selectCount(
                    new LambdaQueryWrapper<WarningRecord>()
                            .ge(WarningRecord::getCreateTime, dayStart)
                            .le(WarningRecord::getCreateTime, dayEnd)
            );
            warningTrend.add(new DashboardVO.TrendItem(date.format(DATE_FORMATTER), count.intValue()));
        }
        dashboard.setWarningTrend(warningTrend);

        return dashboard;
    }

    @Override
    public VisitorStatsVO getVisitorStats(String startDate, String endDate) {
        VisitorStatsVO stats = new VisitorStatsVO();

        LocalDate start = LocalDate.parse(startDate, DATE_FORMATTER);
        LocalDate end = LocalDate.parse(endDate, DATE_FORMATTER);
        LocalDateTime startTime = start.atStartOfDay();
        LocalDateTime endTime = end.atTime(LocalTime.MAX);

        // 总数
        Long totalCount = visitorRecordMapper.selectCount(
                new LambdaQueryWrapper<VisitorRecord>()
                        .ge(VisitorRecord::getCreateTime, startTime)
                        .le(VisitorRecord::getCreateTime, endTime)
        );
        stats.setTotalCount(totalCount.intValue());

        // 待审批数 (status=0)
        Long pendingCount = visitorRecordMapper.selectCount(
                new LambdaQueryWrapper<VisitorRecord>()
                        .eq(VisitorRecord::getStatus, 0)
                        .ge(VisitorRecord::getCreateTime, startTime)
                        .le(VisitorRecord::getCreateTime, endTime)
        );
        stats.setPendingCount(pendingCount.intValue());

        // 已通过数 (status=1)
        Long approvedCount = visitorRecordMapper.selectCount(
                new LambdaQueryWrapper<VisitorRecord>()
                        .eq(VisitorRecord::getStatus, 1)
                        .ge(VisitorRecord::getCreateTime, startTime)
                        .le(VisitorRecord::getCreateTime, endTime)
        );
        stats.setApprovedCount(approvedCount.intValue());

        // 已拒绝数 (status=2)
        Long rejectedCount = visitorRecordMapper.selectCount(
                new LambdaQueryWrapper<VisitorRecord>()
                        .eq(VisitorRecord::getStatus, 2)
                        .ge(VisitorRecord::getCreateTime, startTime)
                        .le(VisitorRecord::getCreateTime, endTime)
        );
        stats.setRejectedCount(rejectedCount.intValue());

        // 每日统计
        List<VisitorStatsVO.DailyStats> dailyStats = new ArrayList<>();
        LocalDate current = start;
        while (!current.isAfter(end)) {
            LocalDateTime dayStart = current.atStartOfDay();
            LocalDateTime dayEnd = current.atTime(LocalTime.MAX);
            Long count = visitorRecordMapper.selectCount(
                    new LambdaQueryWrapper<VisitorRecord>()
                            .ge(VisitorRecord::getCreateTime, dayStart)
                            .le(VisitorRecord::getCreateTime, dayEnd)
            );
            dailyStats.add(new VisitorStatsVO.DailyStats(current.format(DATE_FORMATTER), count.intValue()));
            current = current.plusDays(1);
        }
        stats.setDailyStats(dailyStats);

        return stats;
    }

    @Override
    public DeviceStatsVO getDeviceStats() {
        DeviceStatsVO stats = new DeviceStatsVO();

        // 总数
        Long totalCount = deviceInfoMapper.selectCount(new LambdaQueryWrapper<DeviceInfo>());
        stats.setTotalCount(totalCount.intValue());

        // 正常数 (status=0)
        Long normalCount = deviceInfoMapper.selectCount(
                new LambdaQueryWrapper<DeviceInfo>().eq(DeviceInfo::getStatus, 0)
        );
        stats.setNormalCount(normalCount.intValue());

        // 故障数 (status=1)
        Long faultCount = deviceInfoMapper.selectCount(
                new LambdaQueryWrapper<DeviceInfo>().eq(DeviceInfo::getStatus, 1)
        );
        stats.setFaultCount(faultCount.intValue());

        // 维修数 (status=2)
        Long repairCount = deviceInfoMapper.selectCount(
                new LambdaQueryWrapper<DeviceInfo>().eq(DeviceInfo::getStatus, 2)
        );
        stats.setRepairCount(repairCount.intValue());

        // 报废数 (status=3)
        Long scrapCount = deviceInfoMapper.selectCount(
                new LambdaQueryWrapper<DeviceInfo>().eq(DeviceInfo::getStatus, 3)
        );
        stats.setScrapCount(scrapCount.intValue());

        // 类型分布 - 查询所有设备按类型分组
        List<DeviceInfo> allDevices = deviceInfoMapper.selectList(
                new LambdaQueryWrapper<DeviceInfo>().select(DeviceInfo::getDeviceType)
        );
        Map<String, Long> typeMap = allDevices.stream()
                .filter(d -> d.getDeviceType() != null)
                .collect(Collectors.groupingBy(DeviceInfo::getDeviceType, Collectors.counting()));
        List<DeviceStatsVO.TypeItem> typeDistribution = typeMap.entrySet().stream()
                .map(entry -> new DeviceStatsVO.TypeItem(entry.getKey(), entry.getValue().intValue()))
                .collect(Collectors.toList());
        stats.setTypeDistribution(typeDistribution);

        return stats;
    }

    @Override
    public WarningStatsVO getWarningStats(String startDate, String endDate) {
        WarningStatsVO stats = new WarningStatsVO();

        LocalDate start = LocalDate.parse(startDate, DATE_FORMATTER);
        LocalDate end = LocalDate.parse(endDate, DATE_FORMATTER);
        LocalDateTime startTime = start.atStartOfDay();
        LocalDateTime endTime = end.atTime(LocalTime.MAX);

        // 总数
        Long totalCount = warningRecordMapper.selectCount(
                new LambdaQueryWrapper<WarningRecord>()
                        .ge(WarningRecord::getCreateTime, startTime)
                        .le(WarningRecord::getCreateTime, endTime)
        );
        stats.setTotalCount(totalCount.intValue());

        // 未处理 (status=0)
        Long unhandledCount = warningRecordMapper.selectCount(
                new LambdaQueryWrapper<WarningRecord>()
                        .eq(WarningRecord::getStatus, 0)
                        .ge(WarningRecord::getCreateTime, startTime)
                        .le(WarningRecord::getCreateTime, endTime)
        );
        stats.setUnhandledCount(unhandledCount.intValue());

        // 处理中 (status=1)
        Long handlingCount = warningRecordMapper.selectCount(
                new LambdaQueryWrapper<WarningRecord>()
                        .eq(WarningRecord::getStatus, 1)
                        .ge(WarningRecord::getCreateTime, startTime)
                        .le(WarningRecord::getCreateTime, endTime)
        );
        stats.setHandlingCount(handlingCount.intValue());

        // 已处理 (status=2)
        Long handledCount = warningRecordMapper.selectCount(
                new LambdaQueryWrapper<WarningRecord>()
                        .eq(WarningRecord::getStatus, 2)
                        .ge(WarningRecord::getCreateTime, startTime)
                        .le(WarningRecord::getCreateTime, endTime)
        );
        stats.setHandledCount(handledCount.intValue());

        // 已关闭 (status=3)
        Long closedCount = warningRecordMapper.selectCount(
                new LambdaQueryWrapper<WarningRecord>()
                        .eq(WarningRecord::getStatus, 3)
                        .ge(WarningRecord::getCreateTime, startTime)
                        .le(WarningRecord::getCreateTime, endTime)
        );
        stats.setClosedCount(closedCount.intValue());

        // 级别分布
        List<WarningRecord> allWarnings = warningRecordMapper.selectList(
                new LambdaQueryWrapper<WarningRecord>()
                        .select(WarningRecord::getWarningLevel, WarningRecord::getWarningType)
                        .ge(WarningRecord::getCreateTime, startTime)
                        .le(WarningRecord::getCreateTime, endTime)
        );

        Map<String, Long> levelMap = allWarnings.stream()
                .filter(w -> w.getWarningLevel() != null)
                .collect(Collectors.groupingBy(WarningRecord::getWarningLevel, Collectors.counting()));
        List<WarningStatsVO.LevelItem> levelDistribution = levelMap.entrySet().stream()
                .map(entry -> new WarningStatsVO.LevelItem(entry.getKey(), entry.getValue().intValue()))
                .collect(Collectors.toList());
        stats.setLevelDistribution(levelDistribution);

        // 类型分布
        Map<String, Long> typeMap = allWarnings.stream()
                .filter(w -> w.getWarningType() != null)
                .collect(Collectors.groupingBy(WarningRecord::getWarningType, Collectors.counting()));
        List<WarningStatsVO.TypeItem> typeDistribution = typeMap.entrySet().stream()
                .map(entry -> new WarningStatsVO.TypeItem(entry.getKey(), entry.getValue().intValue()))
                .collect(Collectors.toList());
        stats.setTypeDistribution(typeDistribution);

        // 月度趋势 - 按月统计
        List<WarningStatsVO.MonthlyItem> monthlyTrend = new ArrayList<>();
        LocalDate monthStart = start.withDayOfMonth(1);
        while (!monthStart.isAfter(end)) {
            LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);
            if (monthEnd.isAfter(end)) {
                monthEnd = end;
            }
            LocalDateTime mStart = monthStart.atStartOfDay();
            LocalDateTime mEnd = monthEnd.atTime(LocalTime.MAX);
            Long count = warningRecordMapper.selectCount(
                    new LambdaQueryWrapper<WarningRecord>()
                            .ge(WarningRecord::getCreateTime, mStart)
                            .le(WarningRecord::getCreateTime, mEnd)
            );
            monthlyTrend.add(new WarningStatsVO.MonthlyItem(monthStart.format(MONTH_FORMATTER), count.intValue()));
            monthStart = monthStart.plusMonths(1);
        }
        stats.setMonthlyTrend(monthlyTrend);

        return stats;
    }

    @Override
    public AssetStatsVO getAssetStats() {
        AssetStatsVO stats = new AssetStatsVO();

        // 总数
        Long totalCount = assetInfoMapper.selectCount(new LambdaQueryWrapper<AssetInfo>());
        stats.setTotalCount(totalCount.intValue());

        // 在用 (status=0)
        Long inUseCount = assetInfoMapper.selectCount(
                new LambdaQueryWrapper<AssetInfo>().eq(AssetInfo::getStatus, 0)
        );
        stats.setInUseCount(inUseCount.intValue());

        // 闲置 (status=1)
        Long idleCount = assetInfoMapper.selectCount(
                new LambdaQueryWrapper<AssetInfo>().eq(AssetInfo::getStatus, 1)
        );
        stats.setIdleCount(idleCount.intValue());

        // 报废 (status=2)
        Long scrapCount = assetInfoMapper.selectCount(
                new LambdaQueryWrapper<AssetInfo>().eq(AssetInfo::getStatus, 2)
        );
        stats.setScrapCount(scrapCount.intValue());

        // 维修 (status=3)
        Long repairCount = assetInfoMapper.selectCount(
                new LambdaQueryWrapper<AssetInfo>().eq(AssetInfo::getStatus, 3)
        );
        stats.setRepairCount(repairCount.intValue());

        // 资产总价值
        List<AssetInfo> assets = assetInfoMapper.selectList(
                new LambdaQueryWrapper<AssetInfo>().select(AssetInfo::getAssetValue)
        );
        BigDecimal totalValue = assets.stream()
                .filter(a -> a.getAssetValue() != null)
                .map(AssetInfo::getAssetValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalValue(totalValue);

        return stats;
    }
}
