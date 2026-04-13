package com.campus.safety.module.report.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 仪表盘数据VO
 */
@Data
public class DashboardVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 今日访客数 */
    private Integer todayVisitorCount;

    /** 设备总数 */
    private Integer totalDeviceCount;

    /** 在线设备数 */
    private Integer onlineDeviceCount;

    /** 未处理预警数 */
    private Integer unhandledWarningCount;

    /** 资产总数 */
    private Integer totalAssetCount;

    /** 今日预警数 */
    private Integer todayWarningCount;

    /** 访客趋势(近7天) */
    private List<TrendItem> visitorTrend;

    /** 预警趋势(近7天) */
    private List<TrendItem> warningTrend;

    /**
     * 趋势数据项
     */
    @Data
    public static class TrendItem implements Serializable {
        private static final long serialVersionUID = 1L;

        /** 日期 */
        private String date;

        /** 数量 */
        private Integer count;

        public TrendItem() {}

        public TrendItem(String date, Integer count) {
            this.date = date;
            this.count = count;
        }
    }
}
