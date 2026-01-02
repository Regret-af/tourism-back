package com.af.tourism.exception;

import com.af.tourism.common.ErrorCode;

/**
 * 无权限异常。
 */
public class ForbiddenException extends RuntimeException {
    private final ErrorCode errorCode = ErrorCode.FORBIDDEN;

    public ForbiddenException(String message) {
        super(message);
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
