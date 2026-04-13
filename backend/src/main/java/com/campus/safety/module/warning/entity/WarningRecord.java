package com.campus.safety.module.warning.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.safety.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 预警记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("warning_record")
public class WarningRecord extends BaseEntity {

    /** 预警标题 */
    private String title;

    /** 预警类型 */
    private String warningType;

    /** 预警级别(1-低 2-中 3-高 4-紧急) */
    private String warningLevel;

    /** 预警内容 */
    private String content;

    /** 预警来源 */
    private String source;

    /** 预警位置 */
    private String location;

    /** 处理状态(0-未处理 1-处理中 2-已处理 3-已关闭) */
    private Integer status;

    /** 处理人 */
    private String handler;

    /** 处理时间 */
    private LocalDateTime handleTime;

    /** 处理结果 */
    private String handleResult;

    /** 备注 */
    private String remark;
}
