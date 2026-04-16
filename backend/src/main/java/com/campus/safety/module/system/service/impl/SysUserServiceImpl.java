package com.campus.safety.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.safety.common.exception.BusinessException;
import com.campus.safety.common.utils.PageResult;
import com.campus.safety.module.system.dto.SysUserDTO;
import com.campus.safety.module.system.dto.SysUserQueryDTO;
import com.campus.safety.module.system.entity.*;
import com.campus.safety.module.system.mapper.*;
import com.campus.safety.module.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统用户服务实现
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {

    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysMenuMapper sysMenuMapper;
    private final SysRoleMenuMapper sysRoleMenuMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public SysUser getByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return null;
        }
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        return sysUserMapper.selectOne(wrapper);
    }

    @Override
    public PageResult<SysUser> queryPage(SysUserQueryDTO queryDTO) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryDTO.getUsername()),
                SysUser::getUsername, queryDTO.getUsername());
        wrapper.like(StringUtils.hasText(queryDTO.getNickname()),
                SysUser::getNickname, queryDTO.getNickname());
        wrapper.like(StringUtils.hasText(queryDTO.getPhone()),
                SysUser::getPhone, queryDTO.getPhone());
        wrapper.eq(queryDTO.getStatus() != null,
                SysUser::getStatus, queryDTO.getStatus());
        wrapper.eq(queryDTO.getUserType() != null,
                SysUser::getUserType, queryDTO.getUserType());
        wrapper.orderByDesc(SysUser::getCreateTime);

        Page<SysUser> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        Page<SysUser> result = sysUserMapper.selectPage(page, wrapper);

        // 补齐 roleIds（用于前端“分配角色/编辑回显”）
        List<SysUser> records = result.getRecords();
        if (!CollectionUtils.isEmpty(records)) {
            List<Long> userIds = records.stream().map(SysUser::getId).toList();

            LambdaQueryWrapper<SysUserRole> urWrapper = new LambdaQueryWrapper<>();
            urWrapper.in(SysUserRole::getUserId, userIds);
            List<SysUserRole> userRoles = sysUserRoleMapper.selectList(urWrapper);

            Map<Long, List<Long>> roleIdsMap = userRoles.stream()
                    .collect(Collectors.groupingBy(SysUserRole::getUserId,
                            Collectors.mapping(SysUserRole::getRoleId, Collectors.toList())));

            for (SysUser u : records) {
                u.setRoleIds(roleIdsMap.getOrDefault(u.getId(), Collections.emptyList()));
            }
        }

        return PageResult.of(result.getTotal(), records);
    }

    @Override
    public SysUser getById(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    @Override
    public void add(SysUserDTO dto) {
        // 检查用户名是否重复
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, dto.getUsername());
        Long count = sysUserMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }

        SysUser user = new SysUser();
        BeanUtils.copyProperties(dto, user);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        sysUserMapper.insert(user);

        // 插入用户角色关联
        if (!CollectionUtils.isEmpty(dto.getRoleIds())) {
            for (Long roleId : dto.getRoleIds()) {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(user.getId());
                userRole.setRoleId(roleId);
                sysUserRoleMapper.insert(userRole);
            }
        }
    }

    @Override
    public void update(SysUserDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("用户ID不能为空");
        }
        SysUser existing = sysUserMapper.selectById(dto.getId());
        if (existing == null) {
            throw new BusinessException("用户不存在");
        }

        BeanUtils.copyProperties(dto, existing, "password");
        sysUserMapper.updateById(existing);

        // 更新用户角色关联
        // roleIds == null: 不更新角色关联；roleIds 为空数组: 清空所有角色
        if (dto.getRoleIds() != null) {
            // 先删除旧关联
            LambdaQueryWrapper<SysUserRole> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(SysUserRole::getUserId, dto.getId());
            sysUserRoleMapper.delete(deleteWrapper);

            // 再插入新关联
            if (!CollectionUtils.isEmpty(dto.getRoleIds())) {
                for (Long roleId : dto.getRoleIds()) {
                    SysUserRole userRole = new SysUserRole();
                    userRole.setUserId(dto.getId());
                    userRole.setRoleId(roleId);
                    sysUserRoleMapper.insert(userRole);
                }
            }
        }
    }

    @Override
    public void delete(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        sysUserMapper.deleteById(id);

        // 删除用户角色关联
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, id);
        sysUserRoleMapper.delete(wrapper);
    }

    @Override
    public void resetPassword(Long id, String newPassword) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        sysUserMapper.updateById(user);
    }

    @Override
    public void assignRoles(Long userId, List<Long> roleIds) {
        // 先删除旧关联
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        sysUserRoleMapper.delete(wrapper);

        // 再批量插入新关联
        if (!CollectionUtils.isEmpty(roleIds)) {
            for (Long roleId : roleIds) {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                sysUserRoleMapper.insert(userRole);
            }
        }
    }

    @Override
    public List<String> getRoleKeysByUserId(Long userId) {
        // 查询用户角色关联
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(userRoles)) {
            return Collections.emptyList();
        }

        // 获取角色ID列表
        List<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());

        // 查询角色信息获取roleKey
        LambdaQueryWrapper<SysRole> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.in(SysRole::getId, roleIds);
        List<SysRole> roles = sysRoleMapper.selectList(roleWrapper);
        return roles.stream()
                .map(SysRole::getRoleKey)
                .collect(Collectors.toList());
    }

    @Override
    public Set<String> getPermissionsByUserId(Long userId) {
        // 查询用户角色关联
        LambdaQueryWrapper<SysUserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.eq(SysUserRole::getUserId, userId);
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(userRoleWrapper);
        if (CollectionUtils.isEmpty(userRoles)) {
            return Collections.emptySet();
        }

        // 获取角色ID列表
        List<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());

        // 查询角色菜单关联
        LambdaQueryWrapper<SysRoleMenu> roleMenuWrapper = new LambdaQueryWrapper<>();
        roleMenuWrapper.in(SysRoleMenu::getRoleId, roleIds);
        List<SysRoleMenu> roleMenus = sysRoleMenuMapper.selectList(roleMenuWrapper);
        if (CollectionUtils.isEmpty(roleMenus)) {
            return Collections.emptySet();
        }

        // 获取菜单ID列表
        List<Long> menuIds = roleMenus.stream()
                .map(SysRoleMenu::getMenuId)
                .distinct()
                .collect(Collectors.toList());

        // 查询菜单信息获取权限标识
        LambdaQueryWrapper<SysMenu> menuWrapper = new LambdaQueryWrapper<>();
        menuWrapper.in(SysMenu::getId, menuIds);
        List<SysMenu> menus = sysMenuMapper.selectList(menuWrapper);
        return menus.stream()
                .map(SysMenu::getPerms)
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());
    }
}
