package com.campus.safety.module.system.service;

import com.campus.safety.common.utils.PageResult;
import com.campus.safety.module.system.dto.SysUserDTO;
import com.campus.safety.module.system.dto.SysUserQueryDTO;
import com.campus.safety.module.system.entity.SysUser;

import java.util.List;
import java.util.Set;

/**
 * 系统用户服务接口
 */
public interface SysUserService {

    /**
     * 根据用户名查询用户
     */
    SysUser getByUsername(String username);

    /**
     * 分页查询用户
     */
    PageResult<SysUser> queryPage(SysUserQueryDTO queryDTO);

    /**
     * 根据ID查询用户
     */
    SysUser getById(Long id);

    /**
     * 新增用户
     */
    void add(SysUserDTO dto);

    /**
     * 编辑用户
     */
    void update(SysUserDTO dto);

    /**
     * 删除用户
     */
    void delete(Long id);

    /**
     * 重置密码
     */
    void resetPassword(Long id, String newPassword);

    /**
     * 分配角色
     */
    void assignRoles(Long userId, List<Long> roleIds);

    /**
     * 获取用户角色key列表
     */
    List<String> getRoleKeysByUserId(Long userId);

    /**
     * 获取用户权限标识集合
     */
    Set<String> getPermissionsByUserId(Long userId);
}
