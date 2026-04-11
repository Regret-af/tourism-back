package com.af.tourism.config.converter;

import com.af.tourism.common.enums.DiaryCommentStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * 日记评论状态请求参数转换器
 */
@Component
public class DiaryCommentStatusConverter implements Converter<String, DiaryCommentStatus> {

    @Override
    public DiaryCommentStatus convert(String source) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }
        try {
            return DiaryCommentStatus.fromValue(Integer.valueOf(source));
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
