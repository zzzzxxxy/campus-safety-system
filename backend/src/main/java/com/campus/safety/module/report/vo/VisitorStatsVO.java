package com.campus.safety.module.report.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 访客统计VO
 */
@Data
public class VisitorStatsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 总数 */
    private Integer totalCount;

    /** 待审批数 */
    private Integer pendingCount;

    /** 已通过数 */
    private Integer approvedCount;

    /** 已拒绝数 */
    private Integer rejectedCount;

    /** 每日统计 */
    private List<DailyStats> dailyStats;

    /**
     * 每日统计项
     */
    @Data
    public static class DailyStats implements Serializable {
        private static final long serialVersionUID = 1L;

        /** 日期 */
        private String date;

        /** 数量 */
        private Integer count;

        public DailyStats() {}

        public DailyStats(String date, Integer count) {
            this.date = date;
            this.count = count;
        }
    }
}
