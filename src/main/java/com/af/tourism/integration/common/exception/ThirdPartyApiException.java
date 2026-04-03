package com.af.tourism.integration.common.exception;

import com.af.tourism.common.ErrorCode;

/**
 * 第三方API异常，携带统一错误码。
 */
public class ThirdPartyApiException extends RuntimeException {
    private final ErrorCode errorCode;

    public ThirdPartyApiException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}