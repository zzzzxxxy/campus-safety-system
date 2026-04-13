package com.campus.safety.module.warning.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 预警记录查询DTO
 */
@Data
public class WarningQueryDTO implements Serializable {

    private Integer warningType;

    private Integer warningLevel;

    private String title;

    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
