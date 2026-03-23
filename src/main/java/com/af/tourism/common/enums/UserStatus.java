package com.af.tourism.common.enums;

/**
 * 用户状态类型
 */
public enum UserStatus {
    DISABLED(0),
    ENABLED(1);

    private final int code;

    UserStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static boolean isEnabled(Integer code) {
        return code != null && code == ENABLED.code;
    }

    public static boolean isDisabled(Integer code) {
        return code != null && code == DISABLED.code;
    }
}
