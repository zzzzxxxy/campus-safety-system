package com.campus.safety.module.system.controller;

import com.campus.safety.common.annotation.Log;
import com.campus.safety.common.result.R;
import com.campus.safety.common.utils.PageResult;
import com.campus.safety.module.system.dto.SysUserDTO;
import com.campus.safety.module.system.dto.SysUserQueryDTO;
import com.campus.safety.module.system.entity.SysUser;
import com.campus.safety.module.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/system/user")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 分页查询用户
     */
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('system:user:list')")
    @Log(module = "用户管理", description = "分页查询")
    public R<PageResult<SysUser>> page(SysUserQueryDTO queryDTO) {
        PageResult<SysUser> pageResult = sysUserService.page(queryDTO);
        return R.ok(pageResult);
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:query')")
    @Log(module = "用户管理", description = "查询详情")
    public R<SysUser> getById(@PathVariable Long id) {
        SysUser user = sysUserService.getById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return R.ok(user);
    }

    /**
     * 新增用户
     */
    @PostMapping
    @PreAuthorize("hasAuthority('system:user:add')")
    @Log(module = "用户管理", description = "新增用户")
    public R<Void> add(@RequestBody @Valid SysUserDTO dto) {
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        sysUserService.add(dto);
        return R.ok();
    }

    /**
     * 编辑用户
     */
    @PutMapping
    @PreAuthorize("hasAuthority('system:user:edit')")
    @Log(module = "用户管理", description = "编辑用户")
    public R<Void> update(@RequestBody @Valid SysUserDTO dto) {
        sysUserService.update(dto);
        return R.ok();
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:delete')")
    @Log(module = "用户管理", description = "删除用户")
    public R<Void> delete(@PathVariable Long id) {
        sysUserService.delete(id);
        return R.ok();
    }

    /**
     * 重置密码
     */
    @PutMapping("/{id}/resetPassword")
    @PreAuthorize("hasAuthority('system:user:edit')")
    @Log(module = "用户管理", description = "重置密码")
    public R<Void> resetPassword(@PathVariable Long id, @RequestParam String newPassword) {
        sysUserService.resetPassword(id, passwordEncoder.encode(newPassword));
        return R.ok();
    }
}
