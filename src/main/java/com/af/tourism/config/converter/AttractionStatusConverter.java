package com.af.tourism.config.converter;

import com.af.tourism.common.enums.AttractionStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * 景点状态请求参数转换器
 */
@Component
public class AttractionStatusConverter implements Converter<String, AttractionStatus> {

    @Override
    public AttractionStatus convert(String source) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }
        try {
            return AttractionStatus.fromValue(Integer.valueOf(source));
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
