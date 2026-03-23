package com.af.tourism.pojo.vo;

import com.af.tourism.common.enums.NotificationType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知列表项。
 */
@Data
public class NotificationVO {

    private Long id;
    private NotificationType type;
    private String title;
    private String content;
    private UserPublicVO sender;
    private Long relatedDiaryId;
    private Long relatedCommentId;
    private Boolean isRead;
    private LocalDateTime readTime;
    private LocalDateTime createdAt;
}
