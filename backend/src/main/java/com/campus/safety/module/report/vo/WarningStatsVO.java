package com.campus.safety.module.report.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 预警统计VO
 */
@Data
public class WarningStatsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 总数 */
    private Integer totalCount;

    /** 未处理数 */
    private Integer unhandledCount;

    /** 处理中数 */
    private Integer handlingCount;

    /** 已处理数 */
    private Integer handledCount;

    /** 已关闭数 */
    private Integer closedCount;

    /** 级别分布 */
    private List<LevelItem> levelDistribution;

    /** 类型分布 */
    private List<TypeItem> typeDistribution;

    /** 月度趋势 */
    private List<MonthlyItem> monthlyTrend;

    /**
     * 级别统计项
     */
    @Data
    public static class LevelItem implements Serializable {
        private static final long serialVersionUID = 1L;

        /** 级别 */
        private String level;

        /** 数量 */
        private Integer count;

        public LevelItem() {}

        public LevelItem(String level, Integer count) {
            this.level = level;
            this.count = count;
        }
    }

    /**
     * 类型统计项
     */
    @Data
    public static class TypeItem implements Serializable {
        private static final long serialVersionUID = 1L;

        /** 类型 */
        private String type;

        /** 数量 */
        private Integer count;

        public TypeItem() {}

        public TypeItem(String type, Integer count) {
            this.type = type;
            this.count = count;
        }
    }

    /**
     * 月度统计项
     */
    @Data
    public static class MonthlyItem implements Serializable {
        private static final long serialVersionUID = 1L;

        /** 月份 */
        private String month;

        /** 数量 */
        private Integer count;

        public MonthlyItem() {}

        public MonthlyItem(String month, Integer count) {
            this.month = month;
            this.count = count;
        }
    }
}
