package com.campus.safety.module.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.safety.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统角色实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {

    /** 角色名称 */
    private String roleName;

    /** 角色标识 */
    private String roleKey;

    /** 排序 */
    private Integer sort;

    /** 状态(0-正常 1-禁用) */
    private Integer status;

    /** 备注 */
    private String remark;
}
