package com.af.tourism.config.converter;

import com.af.tourism.common.enums.DiaryStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * 日记状态请求参数转换器
 */
@Component
public class DiaryStatusConverter implements Converter<String, DiaryStatus> {

    @Override
    public DiaryStatus convert(String source) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }
        try {
            return DiaryStatus.fromValue(Integer.valueOf(source));
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
