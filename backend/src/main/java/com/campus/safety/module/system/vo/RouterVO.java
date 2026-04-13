package com.campus.safety.module.system.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 路由VO
 */
@Data
public class RouterVO implements Serializable {

    /** 菜单ID */
    private Long id;

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

    /** 菜单类型 */
    private String menuType;

    /** 权限标识 */
    private String perms;

    /** 图标 */
    private String icon;

    /** 是否可见 */
    private Integer visible;

    /** 子菜单 */
    private List<RouterVO> children;
}
