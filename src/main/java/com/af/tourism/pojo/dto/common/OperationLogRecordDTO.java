package com.af.tourism.pojo.dto.common;

import lombok.Data;

/**
 * 操作日志记录请求。
 */
@Data
public class OperationLogRecordDTO {

    private Long userId;
    private String module;
    private String action;
    private Long bizId;
    private String description;
    private String source;
    private String requestIp;
    private String userAgent;
}
