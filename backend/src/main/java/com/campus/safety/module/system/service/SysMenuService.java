package com.campus.safety.module.system.service;

import com.campus.safety.module.system.dto.SysMenuDTO;
import com.campus.safety.module.system.entity.SysMenu;
import com.campus.safety.module.system.vo.RouterVO;

import java.util.List;

/**
 * 系统菜单服务接口
 */
public interface SysMenuService {

    /**
     * 查询所有菜单
     */
    List<SysMenu> listAll();

    /**
     * 根据ID查询菜单
     */
    SysMenu getById(Long id);

    /**
     * 新增菜单
     */
    void add(SysMenuDTO dto);

    /**
     * 编辑菜单
     */
    void update(SysMenuDTO dto);

    /**
     * 删除菜单
     */
    void delete(Long id);

    /**
     * 构建菜单树
     */
    List<RouterVO> buildMenuTree(List<SysMenu> menus);

    /**
     * 根据角色ID获取菜单ID列表
     */
    List<Long> getMenuIdsByRoleId(Long roleId);
}
