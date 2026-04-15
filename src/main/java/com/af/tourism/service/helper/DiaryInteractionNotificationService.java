package com.af.tourism.service.helper;

import com.af.tourism.common.enums.NotificationType;
import com.af.tourism.converter.NotificationConverter;
import com.af.tourism.mapper.NotificationMapper;
import com.af.tourism.mapper.UserMapper;
import com.af.tourism.pojo.dto.common.DiaryInteractionNotifyCommand;
import com.af.tourism.pojo.entity.Notification;
import com.af.tourism.pojo.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 日记互动通知统一入口
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DiaryInteractionNotificationService {

    private final NotificationMapper notificationMapper;
    private final UserMapper userMapper;

    private final NotificationConverter notificationConverter;

    /**
     * 统一处理日记互动通知
     * @param command 通知命令
     */
    public void notifyInteraction(DiaryInteractionNotifyCommand command) {
        // 1.参数校验
        if (command == null || command.getType() == null) {
            return;
        }
        if (command.getTriggerUserId() == null || command.getRecipientUserId() == null) {
            return;
        }
        if (Objects.equals(command.getTriggerUserId(), command.getRecipientUserId())) {
            return;
        }

        // 2.组装文案
        User triggerUser = userMapper.selectById(command.getTriggerUserId());
        String senderNickname = triggerUser != null && StringUtils.hasText(triggerUser.getNickname())
                ? triggerUser.getNickname()
                : "用户";

        String title = resolveTitle(command.getType());
        String content = resolveContent(command.getType(), senderNickname);
        Notification notification = notificationConverter.toNotification(command, title, content);

        // 3.插入数据库
        notificationMapper.insert(notification);
    }

    /**
     * 组装标题文案
     * @param type 操作类型
     * @return 标题文案
     */
    private String resolveTitle(NotificationType type) {
        if (type == NotificationType.LIKE) {
            return "有人点赞";
        }
        if (type == NotificationType.FAVORITE) {
            return "日记被收藏";
        }
        if (type == NotificationType.COMMENT) {
            return "新的评论";
        }
        return "新的通知";
    }

    /**
     * 组装内容文案
     * @param type 操作类型
     * @param senderNickname 发送昵称
     * @return 内容文案
     */
    private String resolveContent(NotificationType type, String senderNickname) {
        if (type == NotificationType.LIKE) {
            return "用户 " + senderNickname + " 点赞了你的日记";
        }
        if (type == NotificationType.FAVORITE) {
            return "用户 " + senderNickname + " 收藏了你的日记";
        }
        if (type == NotificationType.COMMENT) {
            return "用户 " + senderNickname + " 评论了你的日记";
        }
        return "你收到一条新的互动通知";
    }
}
