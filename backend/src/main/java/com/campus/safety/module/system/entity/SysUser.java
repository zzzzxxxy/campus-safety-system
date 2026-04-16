package com.campus.safety.module.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.safety.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 系统用户实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {

    /** 用户名 */
    private String username;

    /** 密码 */
    private String password;

    /** 昵称 */
    private String nickname;

    /** 手机号 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 头像 */
    private String avatar;

    /** 用户类型(0-管理员 1-教师 2-学生 3-安保) */
    private Integer userType;

    /** 状态(0-正常 1-禁用) */
    private Integer status;

    /** 用户角色ID列表（非表字段） */
    @TableField(exist = false)
    private List<Long> roleIds;
}
