package com.af.tourism.common.enums;

import java.util.Arrays;

/**
 * 日记状态枚举
 */
public enum DiaryStatus {
    OFFLINE(0),
    PUBLIC(1),
    PENDING(2),
    REJECTED(3);

    private final int code;

    DiaryStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static DiaryStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(item -> item.code == code)
                .findFirst()
                .orElse(null);
    }

    public static boolean isValid(Integer code) {
        return fromCode(code) != null;
    }
}
