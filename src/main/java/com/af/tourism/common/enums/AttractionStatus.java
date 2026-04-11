package com.af.tourism.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * 景点状态
 */
public enum AttractionStatus implements OptionProvider<Integer> {
    OFFLINE(0, "下架"),
    ONLINE(1, "上架");

    private final Integer value;
    private final String label;

    AttractionStatus(Integer value, String label) {
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
    public static AttractionStatus fromValue(Integer value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(item -> item.value.equals(value))
                .findFirst()
                .orElse(null);
    }
}
