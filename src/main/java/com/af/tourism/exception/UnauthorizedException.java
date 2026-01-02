package com.af.tourism.exception;

import com.af.tourism.common.ErrorCode;

/**
 * 未登录或 Token 无效异常。
 */
public class UnauthorizedException extends RuntimeException {
    private final ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

    public UnauthorizedException(String message) {
        super(message);
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
