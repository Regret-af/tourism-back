package com.af.tourism.pojo.dto.common;

import com.af.tourism.common.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 日记互动通知命令
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiaryInteractionNotifyCommand {

    private NotificationType type;

    private Long triggerUserId;

    private Long recipientUserId;

    private Long relatedDiaryId;

    private Long relatedCommentId;
}
