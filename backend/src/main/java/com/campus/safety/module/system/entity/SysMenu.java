package com.campus.safety.module.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.safety.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 系统菜单实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
public class SysMenu extends BaseEntity {

    /** 菜单名称 */
    private String menuName;

    /** 父菜单ID */
    private Long parentId;

    /** 显示顺序 */
    private Integer orderNum;

    /** 路由地址 */
    private String path;

    /** 组件路径 */
    private String component;

    /** 菜单类型(M-目录 C-菜单 F-按钮) */
    private String menuType;

    /** 权限标识 */
    private String perms;

    /** 图标 */
    private String icon;

    /** 是否可见(0-显示 1-隐藏) */
    private Integer visible;

    /** 状态(0-正常 1-禁用) */
    private Integer status;

    /** 子菜单 */
    @TableField(exist = false)
    private List<SysMenu> children;
}
