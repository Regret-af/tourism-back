package com.af.tourism.converter;

import com.af.tourism.pojo.dto.common.DiaryInteractionNotifyCommand;
import com.af.tourism.pojo.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationConverter {

    @Mapping(target = "recipientUserId", source = "command.recipientUserId")
    @Mapping(target = "senderUserId", source = "command.triggerUserId")
    @Mapping(target = "type", source = "command.type")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "content", source = "content")
    @Mapping(target = "relatedDiaryId", source = "command.relatedDiaryId")
    @Mapping(target = "relatedCommentId", source = "command.relatedCommentId")
    @Mapping(target = "isRead", constant = "false")
    @Mapping(target = "readTime", expression = "java(null)")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    Notification toNotification(DiaryInteractionNotifyCommand command,
                          String title,
                          String content);
}
