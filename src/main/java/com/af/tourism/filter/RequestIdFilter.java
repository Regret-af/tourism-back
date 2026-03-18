package com.af.tourism.filter;

import com.af.tourism.common.RequestContext;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 为每个请求生成或透传 requestId，便于接口排查和日志追踪。
 */
@Component
public class RequestIdFilter extends OncePerRequestFilter {

    private static final String MDC_KEY = "requestId";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestId = request.getHeader(RequestContext.REQUEST_ID_HEADER);
        if (!StringUtils.hasText(requestId)) {
            requestId = RequestContext.getOrCreateRequestId();
        } else {
            RequestContext.setRequestId(requestId);
        }

        response.setHeader(RequestContext.REQUEST_ID_HEADER, requestId);
        MDC.put(MDC_KEY, requestId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_KEY);
            RequestContext.clear();
        }
    }
}
