package com.af.tourism.config.converter;

import com.af.tourism.common.enums.SortType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToSortTypeConverter implements Converter<String, SortType> {

    @Override
    public SortType convert(String source) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }
        return SortType.fromCode(source.trim());
    }
}