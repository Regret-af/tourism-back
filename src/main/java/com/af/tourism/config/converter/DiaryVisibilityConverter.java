package com.af.tourism.config.converter;

import com.af.tourism.common.enums.DiaryVisibility;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * 日记可见范围请求参数转换器
 */
@Component
public class DiaryVisibilityConverter implements Converter<String, DiaryVisibility> {

    @Override
    public DiaryVisibility convert(String source) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }
        try {
            return DiaryVisibility.fromValue(Integer.valueOf(source));
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
