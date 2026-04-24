package com.af.tourism.service.impl.app;

import com.af.tourism.common.enums.OperationLogSource;
import com.af.tourism.pojo.dto.common.OperationLogRecordDTO;
import com.af.tourism.mq.producer.OperationLogMessageProducer;
import com.af.tourism.security.util.SecurityUtils;
import com.af.tourism.service.app.OperationLogService;
import com.af.tourism.service.helper.OperationLogAsyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class OperationLogServiceImpl implements OperationLogService {

    private final OperationLogMessageProducer operationLogMessageProducer;

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
            request.setUserId(SecurityUtils.getCurrentUserId());
        }

        fillRequestMeta(request);

        try {
            operationLogMessageProducer.send(request);
        } catch (Exception ex) {
            log.warn("发送操作日志 MQ 消息失败，降级为本地异步落库, module={}, action={}, bizId={}",
                    request.getModule(),
                    request.getAction(),
                    request.getBizId(),
                    ex);
            operationLogAsyncService.recordAsync(request);
        }
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

        // 5.填充来源端
        if (!StringUtils.hasText(request.getSource())) {
            request.setSource(resolveSource(httpServletRequest));
        }
    }

    /**
     * 填充来源段
     * @param request Http请求对象
     * @return 来源端
     */
    private String resolveSource(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        if (!StringUtils.hasText(requestURI)) {
            return null;
        }

        if (requestURI.startsWith("/api/v1/admin")) {
            return OperationLogSource.ADMIN.getValue();
        }

        return OperationLogSource.APP.getValue();
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
