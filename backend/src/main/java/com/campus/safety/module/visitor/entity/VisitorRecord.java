package com.campus.safety.module.visitor.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.safety.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 访客记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("visitor_record")
public class VisitorRecord extends BaseEntity {

    /** 访客姓名 */
    private String visitorName;

    /** 访客电话 */
    private String visitorPhone;

    /** 身份证号 */
    private String idCard;

    /** 来访事由 */
    private String reason;

    /** 被访人 */
    private String visitee;

    /** 被访部门 */
    private String department;

    /** 预约到访时间 */
    private LocalDateTime visitTime;

    /** 预约离开时间 */
    private LocalDateTime leaveTime;

    /** 实际到访时间 */
    private LocalDateTime actualVisitTime;

    /** 实际离开时间 */
    private LocalDateTime actualLeaveTime;

    /** 审批状态(0-待审批 1-已通过 2-已拒绝) */
    private Integer status;

    /** 备注 */
    private String remark;
}
