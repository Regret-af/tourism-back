package com.af.tourism.common.enums;

import com.af.tourism.common.ErrorCode;
import com.af.tourism.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * 文件业务类型。
 */
@Slf4j
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

    public static FileBizType fromCode(String code) {
        if (!StringUtils.hasText(code)) {
            log.warn("上传失败，bizType为空，bizType={}", code);
            throw new BusinessException(ErrorCode.PARAM_INVALID, "bizType为空");
        }

        for (FileBizType value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }

        log.warn("上传失败，bizType不支持，bizType={}", code);
        throw new BusinessException(ErrorCode.PARAM_INVALID, "bizType不支持");
    }

}
