package com.af.tourism.common;

/**
 * 统一错误码，收敛到当前 API 定稿约定的编码范围。
 */
public enum ErrorCode {
    OK(0, "success"),
    PARAM_INVALID(40000, "参数错误"),
    UNAUTHORIZED(40100, "未登录或Token无效"),
    FORBIDDEN(40300, "无权限或账号不可用"),
    USER_DISABLED(40300, "账号不可用"),
    NOT_FOUND(40400, "资源不存在"),
    CONFLICT(40900, "资源冲突"),
    BUSINESS_ERROR(42200, "业务处理失败"),
    INTERNAL_ERROR(50000, "系统异常"),
    FILE_UPLOAD_ERROR(50010, "文件上传失败");

    private final int code;
    private final String defaultMessage;

    ErrorCode(int code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public int getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
