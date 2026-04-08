package com.af.tourism.pojo.vo.app;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 单条通知已读响应。
 */
@Data
public class NotificationReadVO {

    private Long id;
    private Boolean isRead;
    private LocalDateTime readTime;
}
