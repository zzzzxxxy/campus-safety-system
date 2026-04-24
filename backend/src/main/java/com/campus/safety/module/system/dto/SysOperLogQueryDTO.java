package com.campus.safety.module.system.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 操作日志查询DTO
 */
@Data
public class SysOperLogQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 模块名称 */
    private String module;

    /** 操作人 */
    private String operUser;

    /** 请求方式 */
    private String requestMethod;

    /** 状态(0正常 1异常) */
    private Integer status;

    /** 开始时间 yyyy-MM-dd HH:mm:ss */
    private String startTime;

    /** 结束时间 yyyy-MM-dd HH:mm:ss */
    private String endTime;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
