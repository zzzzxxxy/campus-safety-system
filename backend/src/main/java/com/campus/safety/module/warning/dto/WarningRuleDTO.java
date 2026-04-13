package com.campus.safety.module.warning.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 预警规则新增/编辑DTO
 */
@Data
public class WarningRuleDTO implements Serializable {

    private Long id;

    @NotBlank(message = "规则名称不能为空")
    private String ruleName;

    private Integer warningType;

    private Integer warningLevel;

    private String ruleCondition;

    private Integer enabled;

    private String remark;
}
