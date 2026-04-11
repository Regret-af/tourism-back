package com.af.tourism.pojo.vo.admin;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理端操作日志列表项
 */
@Data
public class OperationLogForAdminVO {

    private Long id;

    private Long userId;

    private String userNickname;

    private String module;

    private String action;

    private Long bizId;

    private String description;

    private String source;

    private String requestIp;

    private LocalDateTime createdAt;
}
