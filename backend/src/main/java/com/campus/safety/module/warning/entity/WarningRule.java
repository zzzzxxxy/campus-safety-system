package com.campus.safety.module.warning.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.safety.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 预警规则实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("warning_rule")
public class WarningRule extends BaseEntity {

    /** 规则名称 */
    private String ruleName;

    /** 预警类型: 1安全 2设备 3访客 4其他 */
    private Integer warningType;

    /** 预警级别: 1低 2中 3高 4紧急 */
    private Integer warningLevel;

    /** 规则条件 */
    private String ruleCondition;

    /** 启用状态: 0启用 1禁用 */
    private Integer enabled;

    /** 备注 */
    private String remark;
}
