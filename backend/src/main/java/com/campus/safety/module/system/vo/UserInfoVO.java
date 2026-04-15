package com.campus.safety.module.system.vo;

import com.campus.safety.module.system.entity.SysMenu;
import com.campus.safety.module.system.entity.SysUser;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * 用户信息VO
 */
@Data
public class UserInfoVO implements Serializable {

    /** 用户信息 */
    private SysUser user;

    /** 角色key列表 */
    private List<String> roles;

    /** 权限标识集合 */
    private Set<String> permissions;

    /** 菜单树（用于前端动态路由/侧边栏） */
    private List<SysMenu> menus;
}
