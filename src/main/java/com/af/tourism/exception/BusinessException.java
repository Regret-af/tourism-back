package com.af.tourism.exception;

import com.af.tourism.common.ErrorCode;

/**
 * 业务异常，携带统一错误码。
 */
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
