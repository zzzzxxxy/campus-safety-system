package com.campus.safety.module.system.controller;

import com.campus.safety.common.annotation.Log;
import com.campus.safety.common.result.R;
import com.campus.safety.module.system.dto.SysMenuDTO;
import com.campus.safety.module.system.entity.SysMenu;
import com.campus.safety.module.system.service.SysMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/system/menu")
@RequiredArgsConstructor
public class SysMenuController {

    private final SysMenuService sysMenuService;

    /**
     * 获取全部菜单（树形结构）
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:menu:list')")
    @Log(module = "菜单管理", description = "查询列表")
    public R<List<SysMenu>> list() {
        List<SysMenu> list = sysMenuService.listTree();
        return R.ok(list);
    }

    /**
     * 获取菜单详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:query')")
    @Log(module = "菜单管理", description = "查询详情")
    public R<SysMenu> getById(@PathVariable Long id) {
        SysMenu menu = sysMenuService.getById(id);
        return R.ok(menu);
    }

    /**
     * 新增菜单
     */
    @PostMapping
    @PreAuthorize("hasAuthority('system:menu:add')")
    @Log(module = "菜单管理", description = "新增菜单")
    public R<Void> add(@RequestBody @Valid SysMenuDTO dto) {
        sysMenuService.add(dto);
        return R.ok();
    }

    /**
     * 编辑菜单
     */
    @PutMapping
    @PreAuthorize("hasAuthority('system:menu:edit')")
    @Log(module = "菜单管理", description = "编辑菜单")
    public R<Void> update(@RequestBody @Valid SysMenuDTO dto) {
        sysMenuService.update(dto);
        return R.ok();
    }

    /**
     * 删除菜单
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:delete')")
    @Log(module = "菜单管理", description = "删除菜单")
    public R<Void> delete(@PathVariable Long id) {
        sysMenuService.delete(id);
        return R.ok();
    }

    /**
     * 获取角色已分配的菜单ID列表
     */
    @GetMapping("/role/{roleId}")
    @PreAuthorize("hasAuthority('system:menu:list')")
    @Log(module = "菜单管理", description = "查询角色菜单")
    public R<List<Long>> getMenuIdsByRoleId(@PathVariable Long roleId) {
        List<Long> menuIds = sysMenuService.getMenuIdsByRoleId(roleId);
        return R.ok(menuIds);
    }
}
