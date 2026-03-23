package com.af.tourism.common.enums;

import com.af.tourism.common.ErrorCode;
import com.af.tourism.exception.BusinessException;

/**
 * 查询排序类型
 */
public enum SortType {
    LATEST("latest"),
    HOT("hot");

    private final String code;

    SortType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static SortType fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        for (SortType value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        throw new BusinessException(ErrorCode.PARAM_INVALID, "排序类型不支持");
    }

}
