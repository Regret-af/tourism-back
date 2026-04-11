package com.af.tourism.config.converter;

import com.af.tourism.common.enums.DiaryDeletedStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * 日记逻辑删除状态请求参数转换器
 */
@Component
public class DiaryDeletedStatusConverter implements Converter<String, DiaryDeletedStatus> {

    @Override
    public DiaryDeletedStatus convert(String source) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }
        try {
            return DiaryDeletedStatus.fromValue(Integer.valueOf(source));
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
