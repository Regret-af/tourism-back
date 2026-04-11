package com.af.tourism.config.converter;

import com.af.tourism.common.enums.UserStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * 用户状态请求参数转换器
 */
@Component
public class UserStatusConverter implements Converter<String, UserStatus> {

    @Override
    public UserStatus convert(String source) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }
        try {
            return UserStatus.fromValue(Integer.valueOf(source));
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
