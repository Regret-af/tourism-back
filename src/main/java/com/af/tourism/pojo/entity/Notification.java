package com.af.tourism.pojo.entity;

import com.af.tourism.common.enums.NotificationType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 站内通知实体。
 */
@Data
@TableName("notifications")
public class Notification {

    @TableId
    private Long id;

    @TableField("recipient_user_id")
    private Long recipientUserId;

    @TableField("sender_user_id")
    private Long senderUserId;

    private NotificationType type;

    private String title;

    private String content;

    @TableField("related_diary_id")
    private Long relatedDiaryId;

    @TableField("related_comment_id")
    private Long relatedCommentId;

    @TableField("is_read")
    private Integer isRead;

    @TableField("read_time")
    private LocalDateTime readTime;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
