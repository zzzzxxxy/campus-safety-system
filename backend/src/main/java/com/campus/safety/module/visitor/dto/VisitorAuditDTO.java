package com.campus.safety.module.visitor.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 访客审核DTO
 */
@Data
public class VisitorAuditDTO implements Serializable {

    @NotNull(message = "访客记录ID不能为空")
    private Long id;

    @NotNull(message = "审核状态不能为空")
    private Integer status;

    private String remark;
}
