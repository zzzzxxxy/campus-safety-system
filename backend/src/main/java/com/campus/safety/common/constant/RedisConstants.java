package com.campus.safety.common.constant;

/**
 * Redis常量
 */
public class RedisConstants {

    /** 登录token前缀 */
    public static final String LOGIN_TOKEN_KEY = "login:token:";

    /** 登录token过期时间(秒) */
    public static final long LOGIN_TOKEN_TTL = 86400L;

    /** 用户权限缓存前缀 */
    public static final String USER_PERMISSION_KEY = "user:permission:";

    /** 用户角色缓存前缀 */
    public static final String USER_ROLE_KEY = "user:role:";

    /** 验证码前缀 */
    public static final String CAPTCHA_KEY = "captcha:";

    /** 验证码过期时间(秒) */
    public static final long CAPTCHA_TTL = 300L;

    private RedisConstants() {}
}
