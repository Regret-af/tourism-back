package com.af.tourism.pojo.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationReadSyncCommand {

    private Long userId;

    private Long notificationId;

    private LocalDateTime readTime;
}
