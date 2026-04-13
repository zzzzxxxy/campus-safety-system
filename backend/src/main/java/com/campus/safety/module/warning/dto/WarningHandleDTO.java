package com.campus.safety.module.warning.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 预警处理DTO
 */
@Data
public class WarningHandleDTO implements Serializable {

    @NotNull(message = "预警记录ID不能为空")
    private Long id;

    @NotNull(message = "处理状态不能为空")
    private Integer status;

    private String handleResult;
}
