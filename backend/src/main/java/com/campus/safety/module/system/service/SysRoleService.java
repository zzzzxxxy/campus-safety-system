package com.campus.safety.module.system.service;

import com.campus.safety.common.utils.PageResult;
import com.campus.safety.module.system.dto.SysRoleDTO;
import com.campus.safety.module.system.dto.SysRoleQueryDTO;
import com.campus.safety.module.system.entity.SysRole;

import java.util.List;

/**
 * 系统角色服务接口
 */
public interface SysRoleService {

    /**
     * 分页查询角色
     */
    PageResult<SysRole> queryPage(SysRoleQueryDTO queryDTO);

    /**
     * 根据ID查询角色
     */
    SysRole getById(Long id);

    /**
     * 新增角色
     */
    void add(SysRoleDTO dto);

    /**
     * 编辑角色
     */
    void update(SysRoleDTO dto);

    /**
     * 删除角色
     */
    void delete(Long id);

    /**
     * 查询所有角色
     */
    List<SysRole> listAll();

    /**
     * 分配菜单
     */
    void assignMenus(Long roleId, List<Long> menuIds);
}
