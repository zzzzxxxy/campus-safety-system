package com.campus.safety.module.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志实体
 */
@Data
@TableName("sys_oper_log")
public class SysOperLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 模块名称 */
    private String module;

    /** 操作描述 */
    private String description;

    /** 请求方法(类名.方法名) */
    private String method;

    /** 请求方式(GET/POST等) */
    private String requestMethod;

    /** 请求URL */
    private String operUrl;

    /** 操作IP */
    private String operIp;

    /** 请求参数 */
    private String operParam;

    /** 响应结果 */
    private String jsonResult;

    /** 状态(0正常 1异常) */
    private Integer status;

    /** 错误信息 */
    private String errorMsg;

    /** 操作时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operTime;

    /** 操作人 */
    private String operUser;
}
