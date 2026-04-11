package com.af.tourism.config.converter;

import com.af.tourism.common.enums.AttractionCategoryStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * 景点分类状态请求参数转换器
 */
@Component
public class AttractionCategoryStatusConverter implements Converter<String, AttractionCategoryStatus> {

    @Override
    public AttractionCategoryStatus convert(String source) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }
        try {
            return AttractionCategoryStatus.fromValue(Integer.valueOf(source));
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
