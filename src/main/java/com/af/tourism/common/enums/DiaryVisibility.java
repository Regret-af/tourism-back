package com.af.tourism.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * 日记可见范围枚举
 */
public enum DiaryVisibility implements OptionProvider<Integer> {
    PRIVATE(0, "私有"),
    PUBLIC(1, "公开"),
    FRIEND_ONLY(2, "仅好友");

    private final Integer value;
    private final String label;

    DiaryVisibility(Integer value, String label) {
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

    @JsonCreator
    public static DiaryVisibility fromValue(Integer value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(item -> item.value.equals(value))
                .findFirst()
                .orElse(null);
    }
}
