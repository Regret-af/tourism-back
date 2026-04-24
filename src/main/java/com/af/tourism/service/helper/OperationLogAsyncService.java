package com.af.tourism.service.helper;

import com.af.tourism.converter.OperationLogConverter;
import com.af.tourism.mapper.OperationLogMapper;
import com.af.tourism.pojo.dto.common.OperationLogRecordDTO;
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
     * 异步写入操作日志，作为 RabbitMQ 发送失败时的降级方案。
     * @param request 日志 DTO 对象
     */
    @Async("operationLogExecutor")
    public void recordAsync(OperationLogRecordDTO request) {
        try {
            record(request);
        } catch (Exception ex) {
            log.error("操作日志落库失败, module={}, action={}, bizId={}",
                    request == null ? null : request.getModule(),
                    request == null ? null : request.getAction(),
                    request == null ? null : request.getBizId(),
                    ex);
        }
    }

    /**
     * 同步写入操作日志，供 RabbitMQ 消费者复用。
     * @param request 日志 DTO 对象
     */
    public void record(OperationLogRecordDTO request) {
        OperationLog logEntity = operationLogConverter.toOperationLog(request);
        logEntity.setCreatedAt(LocalDateTime.now());
        operationLogMapper.insert(logEntity);
    }
}
