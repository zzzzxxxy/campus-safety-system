package com.campus.safety.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.safety.common.exception.BusinessException;
import com.campus.safety.module.system.entity.SysUser;
import com.campus.safety.module.system.mapper.SysUserMapper;
import com.campus.safety.module.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SysUserDetailsService implements UserDetailsService {

    private final SysUserMapper sysUserMapper;
    private final SysUserService sysUserService;

    public SysUserDetailsService(SysUserMapper sysUserMapper, @Lazy SysUserService sysUserService) {
        this.sysUserMapper = sysUserMapper;
        this.sysUserService = sysUserService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 查询用户 by username
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername, username);
        SysUser user = sysUserMapper.selectOne(queryWrapper);

        // 2. 用户不存在
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        // 3. 用户被禁用
        if (user.getStatus() != 0) {
            throw new BusinessException("用户已被禁用");
        }

        // 4. 获取权限集合
        Set<String> permissions = sysUserService.getPermissionsByUserId(user.getId());

        // 5. 转换为SimpleGrantedAuthority列表
        List<SimpleGrantedAuthority> authorities = permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // 6. 返回SecurityUser
        return new SecurityUser(user, authorities);
    }
}
