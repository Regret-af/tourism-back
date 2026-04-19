package com.af.tourism.service.cache;

import com.af.tourism.common.constants.RedisKeyConstants;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class CacheKeyBuilder {

    public String build(String prefix, Object... parts) {
        List<String> segments = new ArrayList<>();
        segments.add(RedisKeyConstants.ROOT_PREFIX);
        segments.add(prefix);

        if (parts != null) {
            for (Object part : parts) {
                if (part == null) {
                    continue;
                }
                String value = String.valueOf(part).trim();
                if (StringUtils.hasText(value)) {
                    segments.add(value);
                }
            }
        }

        return String.join(":", segments);
    }
}
