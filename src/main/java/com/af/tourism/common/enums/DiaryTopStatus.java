package com.af.tourism.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * 日记置顶状态枚举
 */
public enum DiaryTopStatus implements OptionProvider<Integer> {
    NORMAL(0, "非置顶"),
    TOPPED(1, "置顶");

    private final Integer value;
    private final String label;

    DiaryTopStatus(Integer value, String label) {
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
    public static DiaryTopStatus fromValue(Integer value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(item -> item.value.equals(value))
                .findFirst()
                .orElse(null);
    }
}
