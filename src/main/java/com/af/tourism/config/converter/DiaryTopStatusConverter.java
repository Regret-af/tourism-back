package com.af.tourism.config.converter;

import com.af.tourism.common.enums.DiaryTopStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * 日记置顶状态请求参数转换器
 */
@Component
public class DiaryTopStatusConverter implements Converter<String, DiaryTopStatus> {

    @Override
    public DiaryTopStatus convert(String source) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }
        try {
            return DiaryTopStatus.fromValue(Integer.valueOf(source));
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
