package com.campus.safety.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应状态码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAILED(500, "操作失败"),
    VALIDATE_FAILED(400, "参数校验失败"),
    UNAUTHORIZED(401, "未登录或token已过期"),
    FORBIDDEN(403, "没有相关权限"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    PARAM_ERROR(1001, "参数错误"),
    USER_NOT_FOUND(1002, "用户不存在"),
    USER_DISABLED(1003, "账号已被禁用"),
    USERNAME_OR_PASSWORD_ERROR(1004, "用户名或密码错误"),
    USERNAME_ALREADY_EXISTS(1005, "用户名已存在"),
    TOKEN_EXPIRED(1006, "token已过期"),
    TOKEN_INVALID(1007, "token无效");

    private final int code;
    private final String msg;
}
