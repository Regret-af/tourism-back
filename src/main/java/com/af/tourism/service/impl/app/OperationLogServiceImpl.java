package com.af.tourism.service.impl.app;

import com.af.tourism.pojo.dto.common.OperationLogRecordDTO;
import com.af.tourism.securitylite.AuthContext;
import com.af.tourism.service.app.OperationLogService;
import com.af.tourism.service.helper.OperationLogAsyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 操作日志服务实现。
 */
@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl implements OperationLogService {

    private final OperationLogAsyncService operationLogAsyncService;

    /**
     * 记录用户操作日志
     * @param request 日志DTO对象
     */
    @Override
    public void record(OperationLogRecordDTO request) {
        if (request == null) {
            return;
        }

        if (request.getUserId() == null) {
            request.setUserId(AuthContext.getCurrentUserId());
        }

        fillRequestMeta(request);
        operationLogAsyncService.recordAsync(request);
    }

    /**
     * 填充请求数据
     * @param request 日志DTO对象
     */
    private void fillRequestMeta(OperationLogRecordDTO request) {
        // 1.校验要填充字段，避免重复填充
        if (request.getRequestIp() != null && request.getUserAgent() != null) {
            return;
        }

        // 2.获取当前 Http 请求
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        // 2.1.判断是否存在请求
        if (attributes == null) {
            return;
        }

        // 2.2.获取真实 Http 请求
        HttpServletRequest httpServletRequest = attributes.getRequest();

        // 3.填充 IP
        if (!StringUtils.hasText(request.getRequestIp())) {
            request.setRequestIp(resolveRequestIp(httpServletRequest));
        }

        // 4.填充 User-Agent
        if (!StringUtils.hasText(request.getUserAgent())) {
            request.setUserAgent(httpServletRequest.getHeader("User-Agent"));
        }
    }

    /**
     * 解析真实用户 IP
     * @param request Http请求对象
     * @return 真实 IP
     */
    private String resolveRequestIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(forwardedFor)) {
            int commaIndex = forwardedFor.indexOf(',');
            return commaIndex > -1 ? forwardedFor.substring(0, commaIndex).trim() : forwardedFor.trim();
        }
        return request.getRemoteAddr();
    }
}
