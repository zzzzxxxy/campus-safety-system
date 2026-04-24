package com.campus.safety.module.report.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 资产统计VO
 */
@Data
public class AssetStatsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 总数 */
    private Integer totalCount;

    /** 在用数 */
    private Integer inUseCount;

    /** 闲置数 */
    private Integer idleCount;

    /** 报废数 */
    private Integer scrapCount;

    /** 维修数 */
    private Integer repairCount;

    /** 资产总价值 */
    private BigDecimal totalValue;

    /** 类型分布 */
    private List<TypeItem> typeDistribution;

    /** 区域/位置分布 */
    private List<TypeItem> locationDistribution;

    @Data
    public static class TypeItem implements Serializable {
        private static final long serialVersionUID = 1L;

        private String type;

        private Integer count;

        public TypeItem() {}

        public TypeItem(String type, Integer count) {
            this.type = type;
            this.count = count;
        }
    }
}
