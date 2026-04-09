package com.af.tourism.service.app;

import com.af.tourism.pojo.dto.common.OperationLogRecordDTO;

public interface OperationLogService {

    /**
     * 记录用户操作日志
     * @param request 日志DTO对象
     */
    void record(OperationLogRecordDTO request);
}
