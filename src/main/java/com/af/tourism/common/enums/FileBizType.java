package com.af.tourism.common.enums;

/**
 * 文件业务类型。
 */
public enum FileBizType {
    AVATAR("avatar"),
    DIARY_IMAGE("diary_image");

    private final String code;

    FileBizType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static boolean supports(String code) {
        for (FileBizType value : values()) {
            if (value.code.equals(code)) {
                return true;
            }
        }
        return false;
    }
}
