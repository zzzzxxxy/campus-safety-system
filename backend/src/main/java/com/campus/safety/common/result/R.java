package com.campus.safety.common.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果
 */
@Data
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private int code;
    private String msg;
    private T data;

    private R() {}

    public static <T> R<T> ok() {
        return restResult(null, ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg());
    }

    public static <T> R<T> ok(T data) {
        return restResult(data, ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg());
    }

    public static <T> R<T> ok(T data, String msg) {
        return restResult(data, ResultCode.SUCCESS.getCode(), msg);
    }

    public static <T> R<T> fail() {
        return restResult(null, ResultCode.FAILED.getCode(), ResultCode.FAILED.getMsg());
    }

    public static <T> R<T> fail(String msg) {
        return restResult(null, ResultCode.FAILED.getCode(), msg);
    }

    public static <T> R<T> fail(int code, String msg) {
        return restResult(null, code, msg);
    }

    public static <T> R<T> fail(ResultCode resultCode) {
        return restResult(null, resultCode.getCode(), resultCode.getMsg());
    }

    private static <T> R<T> restResult(T data, int code, String msg) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setData(data);
        r.setMsg(msg);
        return r;
    }
}
