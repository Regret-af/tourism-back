package com.af.tourism.common;

/**
 * 业务统一错误码，部分取自接口文档建议值。
 */
public enum ErrorCode {
    OK(0),
    PARAM_INVALID(40001),
    UNAUTHORIZED(40101),
    FORBIDDEN(40301),
    USER_DISABLED(40302),
    NOT_FOUND(40002),
    INTERNAL_ERROR(50000),
    BUSINESS_ERROR(49999);

    private final int code;

    ErrorCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
