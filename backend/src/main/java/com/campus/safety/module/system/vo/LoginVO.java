package com.campus.safety.module.system.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录返回VO
 */
@Data
public class LoginVO implements Serializable {

    /** 令牌 */
    private String token;

    /** 用户ID */
    private Long userId;

    /** 用户名 */
    private String username;

    /** 昵称 */
    private String nickname;

    /** 头像 */
    private String avatar;

    /** 用户类型 */
    private Integer userType;
}
