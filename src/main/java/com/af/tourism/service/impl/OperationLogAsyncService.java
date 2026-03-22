package com.af.tourism.service.impl;

import com.af.tourism.converter.OperationLogConverter;
import com.af.tourism.mapper.OperationLogMapper;
import com.af.tourism.pojo.dto.OperationLogRecordDTO;
import com.af.tourism.pojo.entity.OperationLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 操作日志异步落库服务。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OperationLogAsyncService {

    private final OperationLogMapper operationLogMapper;

    private final OperationLogConverter operationLogConverter;

    /**
     * 异步填充日志到数据库
     * @param request 日志DTO对象
     */
    @Async("operationLogExecutor")
    public void recordAsync(OperationLogRecordDTO request) {
        try {
            // 1.填充映射字段
            OperationLog logEntity = operationLogConverter.toOperationLog(request);
            // 2.填充默认字段
            logEntity.setCreatedAt(LocalDateTime.now());
            // 3.执行插入操作
            operationLogMapper.insert(logEntity);
        } catch (Exception ex) {
            log.error("操作日志异步落库失败, module={}, action={}, bizId={}",
                    request.getModule(),
                    request.getAction(),
                    request.getBizId(),
                    ex);
        }
    }
}
