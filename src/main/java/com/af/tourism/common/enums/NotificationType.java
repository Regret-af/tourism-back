package com.af.tourism.common.enums;

/**
 * 站内通知类型。
 */
public enum NotificationType {
    COMMENT,
    LIKE,
    FAVORITE,
    SYSTEM;

    public static boolean supports(String value) {
        for (NotificationType type : values()) {
            if (type.name().equals(value)) {
                return true;
            }
        }
        return false;
    }
}
