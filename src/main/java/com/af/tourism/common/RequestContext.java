package com.af.tourism.common;

import cn.hutool.core.util.IdUtil;
import org.springframework.util.StringUtils;

/**
 * 请求级上下文，当前仅维护 requestId。
 */
public final class RequestContext {

    public static final String REQUEST_ID_HEADER = "X-Request-Id";

    private static final ThreadLocal<String> REQUEST_ID_HOLDER = new ThreadLocal<>();

    private RequestContext() {
    }

    public static void setRequestId(String requestId) {
        REQUEST_ID_HOLDER.set(requestId);
    }

    public static String getRequestId() {
        return REQUEST_ID_HOLDER.get();
    }

    public static String getOrCreateRequestId() {
        String requestId = REQUEST_ID_HOLDER.get();
        if (!StringUtils.hasText(requestId)) {
            requestId = IdUtil.fastSimpleUUID();
            REQUEST_ID_HOLDER.set(requestId);
        }
        return requestId;
    }

    public static void clear() {
        REQUEST_ID_HOLDER.remove();
    }
}
