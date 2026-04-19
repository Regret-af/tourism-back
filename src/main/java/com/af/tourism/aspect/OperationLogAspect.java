package com.af.tourism.aspect;

import com.af.tourism.annotation.OperationLogRecord;
import com.af.tourism.common.ApiResponse;
import com.af.tourism.pojo.dto.common.OperationLogRecordDTO;
import com.af.tourism.security.util.SecurityUtils;
import com.af.tourism.service.app.OperationLogService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 操作日志 AOP。
 */
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogService operationLogService;

    @AfterReturning(value = "@annotation(operationLogRecord)", returning = "result")
    public void afterReturning(JoinPoint joinPoint, OperationLogRecord operationLogRecord, Object result) {
        // 1.查看是否成功运行
        if (result instanceof ApiResponse) {
            ApiResponse<?> response = (ApiResponse<?>) result;
            if (response.getCode() != 0) {
                return;
            }
        }

        // 2.构造返回值，填充业务模块和操作
        OperationLogRecordDTO request = new OperationLogRecordDTO();
        request.setModule(operationLogRecord.module().getValue());
        request.setAction(operationLogRecord.action().getValue());
        request.setDescription(operationLogRecord.description());

        // 3.获取操作用户 id
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId != null) {
            request.setUserId(currentUserId);
        } else if (StringUtils.hasText(operationLogRecord.userIdField())) {
            request.setUserId(asLong(extractValue(result, operationLogRecord.userIdField())));
        }

        // 4.获取关联业务 id
        // 4.1.关联业务 id 在参数中，直接取值
        if (operationLogRecord.bizIdArgIndex() >= 0) {
            Object[] args = joinPoint.getArgs();
            int index = operationLogRecord.bizIdArgIndex();
            if (args != null && index < args.length) {
                request.setBizId(asLong(args[index]));
            }
        } else if (StringUtils.hasText(operationLogRecord.bizIdField())) {
            // 4.2.关联业务 id 不在参数中，取传参值
            request.setBizId(asLong(extractValue(result, operationLogRecord.bizIdField())));
        }

        operationLogService.record(request);
    }

    /**
     * 获取字段值
     * @param target 要读取的对象
     * @param fieldPath 字段路径
     * @return
     */
    private Object extractValue(Object target, String fieldPath) {
        try {
            BeanWrapperImpl beanWrapper = new BeanWrapperImpl(target);
            return beanWrapper.getPropertyValue(fieldPath);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 转换为 Long 类型
     * @param value 值
     * @return Long类型值
     */
    private Long asLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return Long.valueOf(String.valueOf(value));
    }
}
