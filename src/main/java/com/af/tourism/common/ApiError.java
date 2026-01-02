package com.af.tourism.common;

/**
 * 统一错误描述对象，可在异常处理中返回。
 */
public class ApiError {
    private final int code;
    private final String message;

    public ApiError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
