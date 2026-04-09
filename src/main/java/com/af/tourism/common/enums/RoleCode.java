package com.af.tourism.common.enums;

import java.util.Arrays;

/**
 * 角色编码类型
 */
public enum RoleCode {
    USER(1),
    ADMIN(2);

    Integer value;

    RoleCode(int vale) {
        this.value = vale;
    }

    public Integer getValue() {
        return value;
    }

    public static RoleCode fromCode(String code) {
        return Arrays.stream(values())
                .filter(item -> item.name().equals(code))
                .findFirst()
                .orElse(null);
    }
}
