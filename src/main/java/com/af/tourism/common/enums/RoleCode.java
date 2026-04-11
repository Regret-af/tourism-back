package com.af.tourism.common.enums;

import java.util.Arrays;

/**
 * 角色编码类型
 */
public enum RoleCode implements OptionProvider<String> {
    USER("USER", "普通用户", 1),
    ADMIN("ADMIN", "管理员", 2);

    private final String value;
    private final String label;
    private final Integer level;

    RoleCode(String value, String label, Integer level) {
        this.value = value;
        this.label = label;
        this.level = level;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public Integer getLevel() {
        return level;
    }

    public String getCode() {
        return value;
    }

    public static RoleCode fromCode(String code) {
        return Arrays.stream(values())
                .filter(item -> item.value.equals(code))
                .findFirst()
                .orElse(null);
    }
}
