package com.af.tourism.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * 用户状态枚举
 */
public enum UserStatus implements OptionProvider<Integer> {
    DISABLED(0, "禁用"),
    ENABLED(1, "正常");

    private final Integer value;
    private final String label;

    UserStatus(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    @Override
    @JsonValue
    public Integer getValue() {
        return value;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public Integer getCode() {
        return value;
    }

    @JsonCreator
    public static UserStatus fromValue(Integer value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(item -> item.value.equals(value))
                .findFirst()
                .orElse(null);
    }

    public static boolean isEnabled(Integer code) {
        return code != null && code.equals(ENABLED.value);
    }

    public static boolean isDisabled(Integer code) {
        return code != null && code.equals(DISABLED.value);
    }
}
