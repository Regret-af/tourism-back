package com.af.tourism.config.converter;

import com.af.tourism.common.enums.RoleCode;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * 角色编码请求参数转换器
 */
@Component
public class RoleCodeConverter implements Converter<String, RoleCode> {

    @Override
    public RoleCode convert(String source) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }
        return RoleCode.fromCode(source.trim());
    }
}
