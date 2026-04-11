package com.af.tourism.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * 日记逻辑删除状态枚举
 */
public enum DiaryDeletedStatus implements OptionProvider<Integer> {
    NOT_DELETED(0, "未删除"),
    DELETED(1, "已删除");

    private final Integer value;
    private final String label;

    DiaryDeletedStatus(Integer value, String label) {
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
    public static DiaryDeletedStatus fromValue(Integer value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(item -> item.value.equals(value))
                .findFirst()
                .orElse(null);
    }
}
