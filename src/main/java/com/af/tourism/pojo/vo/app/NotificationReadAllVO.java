package com.af.tourism.pojo.vo.app;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationReadAllVO {

    private Long readCount;

    private Long unreadCount;

    private LocalDateTime readTime;
}
