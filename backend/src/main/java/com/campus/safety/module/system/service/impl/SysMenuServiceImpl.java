package com.campus.safety.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.safety.common.exception.BusinessException;
import com.campus.safety.module.system.dto.SysMenuDTO;
import com.campus.safety.module.system.entity.SysMenu;
import com.campus.safety.module.system.entity.SysRoleMenu;
import com.campus.safety.module.system.mapper.SysMenuMapper;
import com.campus.safety.module.system.mapper.SysRoleMenuMapper;
import com.campus.safety.module.system.service.SysMenuService;
import com.campus.safety.module.system.vo.RouterVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统菜单服务实现
 */
@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl implements SysMenuService {

    private final SysMenuMapper sysMenuMapper;
    private final SysRoleMenuMapper sysRoleMenuMapper;

    @Override
    public List<SysMenu> listAll() {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysMenu::getOrderNum);
        return sysMenuMapper.selectList(wrapper);
    }

    @Override
    public SysMenu getById(Long id) {
        SysMenu menu = sysMenuMapper.selectById(id);
        if (menu == null) {
            throw new BusinessException("菜单不存在");
        }
        return menu;
    }

    @Override
    public void add(SysMenuDTO dto) {
        SysMenu menu = new SysMenu();
        BeanUtils.copyProperties(dto, menu);
        sysMenuMapper.insert(menu);
    }

    @Override
    public void update(SysMenuDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("菜单ID不能为空");
        }
        SysMenu existing = sysMenuMapper.selectById(dto.getId());
        if (existing == null) {
            throw new BusinessException("菜单不存在");
        }
        BeanUtils.copyProperties(dto, existing);
        sysMenuMapper.updateById(existing);
    }

    @Override
    public void delete(Long id) {
        SysMenu menu = sysMenuMapper.selectById(id);
        if (menu == null) {
            throw new BusinessException("菜单不存在");
        }

        // 检查是否有子菜单
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId, id);
        Long count = sysMenuMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("存在子菜单，无法删除");
        }

        sysMenuMapper.deleteById(id);

        // 删除角色菜单关联
        LambdaQueryWrapper<SysRoleMenu> roleMenuWrapper = new LambdaQueryWrapper<>();
        roleMenuWrapper.eq(SysRoleMenu::getMenuId, id);
        sysRoleMenuMapper.delete(roleMenuWrapper);
    }

    @Override
    public List<RouterVO> buildMenuTree(List<SysMenu> menus) {
        // 转换为RouterVO列表
        List<RouterVO> routerList = menus.stream().map(menu -> {
            RouterVO router = new RouterVO();
            router.setId(menu.getId());
            router.setMenuName(menu.getMenuName());
            router.setParentId(menu.getParentId());
            router.setOrderNum(menu.getOrderNum());
            router.setPath(menu.getPath());
            router.setComponent(menu.getComponent());
            router.setMenuType(menu.getMenuType());
            router.setPerms(menu.getPerms());
            router.setIcon(menu.getIcon());
            router.setVisible(menu.getVisible());
            router.setChildren(new ArrayList<>());
            return router;
        }).collect(Collectors.toList());

        // 构建树形结构，parentId==0为根节点
        List<RouterVO> tree = new ArrayList<>();
        for (RouterVO router : routerList) {
            if (router.getParentId() == null || router.getParentId() == 0L) {
                tree.add(router);
            }
        }

        // 递归设置子节点
        for (RouterVO parent : tree) {
            buildChildren(parent, routerList);
        }

        return tree;
    }

    /**
     * 递归构建子节点
     */
    private void buildChildren(RouterVO parent, List<RouterVO> allRouters) {
        for (RouterVO router : allRouters) {
            if (parent.getId().equals(router.getParentId())) {
                parent.getChildren().add(router);
                buildChildren(router, allRouters);
            }
        }
    }

    @Override
    public List<Long> getMenuIdsByRoleId(Long roleId) {
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, roleId);
        List<SysRoleMenu> roleMenus = sysRoleMenuMapper.selectList(wrapper);
        return roleMenus.stream()
                .map(SysRoleMenu::getMenuId)
                .collect(Collectors.toList());
    }
}
