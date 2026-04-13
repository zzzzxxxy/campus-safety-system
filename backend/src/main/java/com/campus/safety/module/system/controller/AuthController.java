package com.campus.safety.module.system.controller;

import com.campus.safety.common.exception.BusinessException;
import com.campus.safety.common.result.R;
import com.campus.safety.common.result.ResultCode;
import com.campus.safety.common.utils.JwtUtils;
import com.campus.safety.module.system.dto.LoginDTO;
import com.campus.safety.module.system.dto.RegisterDTO;
import com.campus.safety.module.system.dto.SysUserDTO;
import com.campus.safety.module.system.entity.SysUser;
import com.campus.safety.module.system.service.SysUserService;
import com.campus.safety.module.system.vo.LoginVO;
import com.campus.safety.module.system.vo.UserInfoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SysUserService sysUserService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public R<LoginVO> login(@RequestBody @Valid LoginDTO dto) {
        // 1. 查询用户 by username
        SysUser user = sysUserService.getByUsername(dto.getUsername());

        // 2. 检查用户是否存在
        if (user == null) {
            return R.fail(ResultCode.USER_NOT_FOUND.getCode(), ResultCode.USER_NOT_FOUND.getMsg());
        }

        // 3. 检查用户状态
        if (user.getStatus() != 0) {
            return R.fail(ResultCode.USER_DISABLED.getCode(), ResultCode.USER_DISABLED.getMsg());
        }

        // 4. 认证
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            return R.fail(ResultCode.USERNAME_OR_PASSWORD_ERROR.getCode(), ResultCode.USERNAME_OR_PASSWORD_ERROR.getMsg());
        }

        // 5. 生成token
        String token = jwtUtils.generateToken(user.getUsername(), user.getId());

        // 6. 构建LoginVO返回
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUserId(user.getId());
        loginVO.setUsername(user.getUsername());
        loginVO.setNickname(user.getNickname());

        return R.ok(loginVO);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public R<Void> register(@RequestBody @Valid RegisterDTO dto) {
        // 检查用户名重复
        SysUser existUser = sysUserService.getByUsername(dto.getUsername());
        if (existUser != null) {
            return R.fail(ResultCode.USERNAME_ALREADY_EXISTS.getCode(), ResultCode.USERNAME_ALREADY_EXISTS.getMsg());
        }

        // 创建SysUserDTO
        SysUserDTO userDTO = new SysUserDTO();
        userDTO.setUsername(dto.getUsername());
        userDTO.setPassword(passwordEncoder.encode(dto.getPassword()));
        userDTO.setNickname(dto.getNickname());
        userDTO.setStatus(0);

        // 调用service新增
        sysUserService.add(userDTO);

        return R.ok();
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/info")
    public R<UserInfoVO> getInfo() {
        // 从SecurityContextHolder获取当前用户名
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // 查询用户
        SysUser user = sysUserService.getByUsername(username);
        if (user == null) {
            return R.fail(ResultCode.USER_NOT_FOUND.getCode(), ResultCode.USER_NOT_FOUND.getMsg());
        }

        // 查询角色keys
        Set<String> roles = sysUserService.getRoleKeysByUserId(user.getId());

        // 查询权限perms
        Set<String> permissions = sysUserService.getPermissionsByUserId(user.getId());

        // 构建UserInfoVO
        user.setPassword(null);
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setUser(user);
        userInfoVO.setRoles(roles);
        userInfoVO.setPermissions(permissions);

        return R.ok(userInfoVO);
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public R<Void> logout() {
        return R.ok();
    }
}
