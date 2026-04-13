package com.campus.safety.module.warning.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 预警规则查询DTO
 */
@Data
public class WarningRuleQueryDTO implements Serializable {

    private String ruleName;

    private Integer warningType;

    private Integer warningLevel;

    private Integer enabled;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
