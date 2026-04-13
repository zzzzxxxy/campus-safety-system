package com.campus.safety.module.asset.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.safety.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 资产信息实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("asset_info")
public class AssetInfo extends BaseEntity {

    /** 资产名称 */
    private String assetName;

    /** 资产编号 */
    private String assetCode;

    /** 资产类型 */
    private String assetType;

    /** 资产价值 */
    private BigDecimal assetValue;

    /** 购置日期 */
    private LocalDate purchaseDate;

    /** 存放位置 */
    private String location;

    /** 使用部门 */
    private String department;

    /** 负责人 */
    private String responsible;

    /** 资产状态(0-在用 1-闲置 2-报废 3-维修) */
    private Integer status;

    /** 备注 */
    private String remark;
}
