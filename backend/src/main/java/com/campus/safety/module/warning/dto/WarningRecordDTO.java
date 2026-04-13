package com.campus.safety.module.warning.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 预警记录新增/编辑DTO
 */
@Data
public class WarningRecordDTO implements Serializable {

    private Long id;

    private Integer warningType;

    private Integer warningLevel;

    @NotBlank(message = "预警标题不能为空")
    private String title;

    private String content;

    private String source;

    private String location;
}
