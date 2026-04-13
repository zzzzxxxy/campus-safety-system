package com.campus.safety.module.report.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 设备统计VO
 */
@Data
public class DeviceStatsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 总数 */
    private Integer totalCount;

    /** 正常数 */
    private Integer normalCount;

    /** 故障数 */
    private Integer faultCount;

    /** 维修数 */
    private Integer repairCount;

    /** 报废数 */
    private Integer scrapCount;

    /** 类型分布 */
    private List<TypeItem> typeDistribution;

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
}
