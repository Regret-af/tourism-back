package com.af.tourism.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * 日记评论状态枚举
 */
public enum DiaryCommentStatus implements OptionProvider<Integer> {
    HIDDEN(0, "隐藏"),
    NORMAL(1, "正常");

    private final Integer value;
    private final String label;

    DiaryCommentStatus(Integer value, String label) {
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
    public static DiaryCommentStatus fromValue(Integer value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(item -> item.value.equals(value))
                .findFirst()
                .orElse(null);
    }
}
