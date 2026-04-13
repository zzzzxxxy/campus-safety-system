package com.campus.safety.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.safety.common.exception.BusinessException;
import com.campus.safety.common.utils.PageResult;
import com.campus.safety.module.system.dto.SysRoleDTO;
import com.campus.safety.module.system.dto.SysRoleQueryDTO;
import com.campus.safety.module.system.entity.SysRole;
import com.campus.safety.module.system.entity.SysRoleMenu;
import com.campus.safety.module.system.mapper.SysRoleMapper;
import com.campus.safety.module.system.mapper.SysRoleMenuMapper;
import com.campus.safety.module.system.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 系统角色服务实现
 */
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl implements SysRoleService {

    private final SysRoleMapper sysRoleMapper;
    private final SysRoleMenuMapper sysRoleMenuMapper;

    @Override
    public PageResult<SysRole> queryPage(SysRoleQueryDTO queryDTO) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryDTO.getRoleName()),
                SysRole::getRoleName, queryDTO.getRoleName());
        wrapper.like(StringUtils.hasText(queryDTO.getRoleKey()),
                SysRole::getRoleKey, queryDTO.getRoleKey());
        wrapper.eq(queryDTO.getStatus() != null,
                SysRole::getStatus, queryDTO.getStatus());
        wrapper.orderByAsc(SysRole::getSort);

        Page<SysRole> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        Page<SysRole> result = sysRoleMapper.selectPage(page, wrapper);
        return PageResult.of(result.getTotal(), result.getRecords());
    }

    @Override
    public SysRole getById(Long id) {
        SysRole role = sysRoleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        return role;
    }

    @Override
    public void add(SysRoleDTO dto) {
        SysRole role = new SysRole();
        BeanUtils.copyProperties(dto, role);
        sysRoleMapper.insert(role);

        // 插入角色菜单关联
        if (!CollectionUtils.isEmpty(dto.getMenuIds())) {
            for (Long menuId : dto.getMenuIds()) {
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setRoleId(role.getId());
                roleMenu.setMenuId(menuId);
                sysRoleMenuMapper.insert(roleMenu);
            }
        }
    }

    @Override
    public void update(SysRoleDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("角色ID不能为空");
        }
        SysRole existing = sysRoleMapper.selectById(dto.getId());
        if (existing == null) {
            throw new BusinessException("角色不存在");
        }

        BeanUtils.copyProperties(dto, existing);
        sysRoleMapper.updateById(existing);

        // 更新角色菜单关联
        if (!CollectionUtils.isEmpty(dto.getMenuIds())) {
            // 先删除旧关联
            LambdaQueryWrapper<SysRoleMenu> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(SysRoleMenu::getRoleId, dto.getId());
            sysRoleMenuMapper.delete(deleteWrapper);

            // 再插入新关联
            for (Long menuId : dto.getMenuIds()) {
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setRoleId(dto.getId());
                roleMenu.setMenuId(menuId);
                sysRoleMenuMapper.insert(roleMenu);
            }
        }
    }

    @Override
    public void delete(Long id) {
        SysRole role = sysRoleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        sysRoleMapper.deleteById(id);

        // 删除角色菜单关联
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, id);
        sysRoleMenuMapper.delete(wrapper);
    }

    @Override
    public List<SysRole> listAll() {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysRole::getSort);
        return sysRoleMapper.selectList(wrapper);
    }

    @Override
    public void assignMenus(Long roleId, List<Long> menuIds) {
        // 先删除旧关联
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, roleId);
        sysRoleMenuMapper.delete(wrapper);

        // 再批量插入新关联
        if (!CollectionUtils.isEmpty(menuIds)) {
            for (Long menuId : menuIds) {
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menuId);
                sysRoleMenuMapper.insert(roleMenu);
            }
        }
    }
}
