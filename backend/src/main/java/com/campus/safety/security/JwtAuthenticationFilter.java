package com.campus.safety.security;

import com.campus.safety.common.constant.RedisConstants;
import com.campus.safety.common.utils.JwtUtils;
import com.campus.safety.module.system.mapper.SysMenuMapper;
import com.campus.safety.module.system.mapper.SysRoleMenuMapper;
import com.campus.safety.module.system.mapper.SysUserRoleMapper;
import com.campus.safety.module.system.entity.SysMenu;
import com.campus.safety.module.system.entity.SysRoleMenu;
import com.campus.safety.module.system.entity.SysUserRole;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.HashSet;

/**
 * JWT认证过滤器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final RedisTemplate<String, Object> redisTemplate;

    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMenuMapper sysRoleMenuMapper;
    private final SysMenuMapper sysMenuMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);
        if (log.isDebugEnabled()) {
            log.debug("JWT filter: uri={} hasToken={} authHeaderPresent={}",
                    request.getRequestURI(),
                    StringUtils.hasText(token),
                    StringUtils.hasText(request.getHeader("Authorization")));
        }

        if (StringUtils.hasText(token) && jwtUtils.validateToken(token)) {
            String username = jwtUtils.getUsernameFromToken(token);
            Long userId = jwtUtils.getUserIdFromToken(token);

            // 检查Redis中是否存在该token（用于支持登出）
            // 注意：如果登录时没有把token写入Redis，这里会导致所有请求都无法通过认证。
            // 为了让系统“无Redis会话”也能工作：
            // - Redis里有token：按原逻辑校验（支持登出/踢下线）
            // - Redis里没有token：仍然允许基于JWT本身通过认证（退化为纯JWT无状态模式）
            String redisKey = RedisConstants.LOGIN_TOKEN_KEY + userId;
            Object cachedToken = redisTemplate.opsForValue().get(redisKey);

            // 获取用户权限（优先走缓存；缓存缺失则回源DB，确保“纯JWT无Redis”模式也可用）
            List<SimpleGrantedAuthority> authorities = getAuthorities(userId);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求头解析token
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 从Redis获取用户权限
     */
    @SuppressWarnings("unchecked")
    private List<SimpleGrantedAuthority> getAuthorities(Long userId) {
        // 说明：当前项目 RedisTemplate 的 value serializer 可能启用了 Jackson 多态（WRAPPER_ARRAY），
        // 若 Redis 中历史数据是“纯数组 JSON”（例如 ["system:role:list", ...]），反序列化会把第一个元素当作 typeId 导致 InvalidTypeIdException。
        // 为避免出现“接口偶发 401/500”，这里对 Redis 读取做强兜底：读失败或不存在均回源 DB。
        List<String> permissions = null;
        try {
            String permKey = RedisConstants.USER_PERMISSION_KEY + userId;
            permissions = (List<String>) redisTemplate.opsForValue().get(permKey);
        } catch (Exception ignore) {
            permissions = null;
        }

        if (permissions == null) {
            try {
                permissions = List.copyOf(loadPermissionsFromDb(userId));
            } catch (Exception e) {
                permissions = Collections.emptyList();
            }
        }

        return permissions.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    /**
     * 从DB回源查询权限（等价于 SysUserServiceImpl#getPermissionsByUserId，但避免注入 Service 形成循环依赖）
     */
    private Set<String> loadPermissionsFromDb(Long userId) {
        // 1) user -> roleIds
        LambdaQueryWrapper<SysUserRole> urWrapper = new LambdaQueryWrapper<>();
        urWrapper.eq(SysUserRole::getUserId, userId);
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(urWrapper);
        if (userRoles == null || userRoles.isEmpty()) {
            return Collections.emptySet();
        }
        List<Long> roleIds = userRoles.stream().map(SysUserRole::getRoleId).distinct().collect(Collectors.toList());

        // 2) roleIds -> menuIds
        LambdaQueryWrapper<SysRoleMenu> rmWrapper = new LambdaQueryWrapper<>();
        rmWrapper.in(SysRoleMenu::getRoleId, roleIds);
        List<SysRoleMenu> roleMenus = sysRoleMenuMapper.selectList(rmWrapper);
        if (roleMenus == null || roleMenus.isEmpty()) {
            return Collections.emptySet();
        }
        List<Long> menuIds = roleMenus.stream().map(SysRoleMenu::getMenuId).distinct().collect(Collectors.toList());

        // 3) menuIds -> perms
        LambdaQueryWrapper<SysMenu> menuWrapper = new LambdaQueryWrapper<>();
        menuWrapper.in(SysMenu::getId, menuIds);
        List<SysMenu> menus = sysMenuMapper.selectList(menuWrapper);
        if (menus == null || menus.isEmpty()) {
            return Collections.emptySet();
        }

        Set<String> perms = new HashSet<>();
        for (SysMenu m : menus) {
            if (m != null && StringUtils.hasText(m.getPerms())) {
                perms.add(m.getPerms());
            }
        }
        return perms;
    }
}

