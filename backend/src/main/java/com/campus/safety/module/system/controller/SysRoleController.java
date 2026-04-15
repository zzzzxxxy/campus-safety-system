package com.campus.safety.module.system.controller;

import com.campus.safety.common.annotation.Log;
import com.campus.safety.common.result.R;
import com.campus.safety.common.utils.PageResult;
import com.campus.safety.module.system.dto.SysRoleDTO;
import com.campus.safety.module.system.dto.SysRoleQueryDTO;
import com.campus.safety.module.system.entity.SysRole;
import com.campus.safety.module.system.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/system/role")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleService sysRoleService;

    /**
     * 分页查询角色
     */
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('system:role:list')")
    @Log(module = "角色管理", description = "分页查询")
    public R<PageResult<SysRole>> page(SysRoleQueryDTO queryDTO) {
        PageResult<SysRole> pageResult = sysRoleService.queryPage(queryDTO);
        return R.ok(pageResult);
    }

    /**
     * 获取全部角色列表（不分页）
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:role:list')")
    @Log(module = "角色管理", description = "查询列表")
    public R<List<SysRole>> list() {
        List<SysRole> list = sysRoleService.listAll();
        return R.ok(list);
    }

    /**
     * 获取角色详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:query')")
    @Log(module = "角色管理", description = "查询详情")
    public R<SysRole> getById(@PathVariable Long id) {
        SysRole role = sysRoleService.getById(id);
        return R.ok(role);
    }

    /**
     * 新增角色
     */
    @PostMapping
    @PreAuthorize("hasAuthority('system:role:add')")
    @Log(module = "角色管理", description = "新增角色")
    public R<Void> add(@RequestBody @Valid SysRoleDTO dto) {
        sysRoleService.add(dto);
        return R.ok();
    }

    /**
     * 编辑角色
     */
    @PutMapping
    @PreAuthorize("hasAuthority('system:role:edit')")
    @Log(module = "角色管理", description = "编辑角色")
    public R<Void> update(@RequestBody @Valid SysRoleDTO dto) {
        sysRoleService.update(dto);
        return R.ok();
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:delete')")
    @Log(module = "角色管理", description = "删除角色")
    public R<Void> delete(@PathVariable Long id) {
        sysRoleService.delete(id);
        return R.ok();
    }
}
