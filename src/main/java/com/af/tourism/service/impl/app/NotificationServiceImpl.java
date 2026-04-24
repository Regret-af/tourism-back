package com.af.tourism.service.impl.app;

import com.af.tourism.common.ErrorCode;
import com.af.tourism.exception.BusinessException;
import com.af.tourism.mapper.NotificationMapper;
import com.af.tourism.mq.producer.NotificationReadMessageProducer;
import com.af.tourism.pojo.dto.app.NotificationQueryDTO;
import com.af.tourism.pojo.dto.common.NotificationReadSyncCommand;
import com.af.tourism.pojo.entity.Notification;
import com.af.tourism.pojo.vo.app.NotificationReadVO;
import com.af.tourism.pojo.vo.app.NotificationUnreadCountVO;
import com.af.tourism.pojo.vo.app.NotificationVO;
import com.af.tourism.pojo.vo.common.PageResponse;
import com.af.tourism.service.app.NotificationService;
import com.af.tourism.service.cache.NotificationUnreadCacheSupport;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知服务实现。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;
    private final NotificationUnreadCacheSupport notificationUnreadCacheSupport;
    private final NotificationReadMessageProducer notificationReadMessageProducer;

    /**
     * 查询用户通知列表
     * @param userId 用户 id
     * @param queryDTO 查询参数
     * @return 用户通知列表
     */
    @Override
    public PageResponse<NotificationVO> listNotifications(Long userId, NotificationQueryDTO queryDTO) {
        // 1.开启分页查询
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        // 2.执行查询
        List<NotificationVO> list = notificationMapper.selectNotificationPage(userId, queryDTO);
        PageInfo<NotificationVO> pageInfo = new PageInfo<>(list);

        PageResponse<NotificationVO> response = new PageResponse<>();
        response.setList(list);
        response.setPageNum(pageInfo.getPageNum());
        response.setPageSize(pageInfo.getPageSize());
        response.setTotal(pageInfo.getTotal());

        return response;
    }

    /**
     * 获取用户未读通知数量
     * @param userId 用户 id
     * @return 未读通知数量
     */
    @Override
    public NotificationUnreadCountVO getUnreadCount(Long userId) {
        NotificationUnreadCountVO response = new NotificationUnreadCountVO();
        response.setUnreadCount(notificationUnreadCacheSupport.getUnreadCount(
                userId,
                () -> notificationMapper.countUnreadByRecipientUserId(userId)
        ));

        return response;
    }

    /**
     * 标记通知已读
     * @param userId 用户 id
     * @param notificationId 通知 id
     * @return 已读信息
     */
    @Override
    public NotificationReadVO markAsRead(Long userId, Long notificationId) {
        // 1.查找并校验通知信息
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification == null) {
            log.warn("通知不存在，userId={}, notificationId={}", userId, notificationId);
            throw new BusinessException(ErrorCode.NOT_FOUND, "通知不存在");
        }
        if (notification.getRecipientUserId() == null || !notification.getRecipientUserId().equals(userId)) {
            log.warn("通知不属于当前用户，userId={}, notificationId={}", userId, notificationId);
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "通知不属于当前用户");
        }

        // 2.查看是否为未读状态
        LocalDateTime readTime = notification.getReadTime();
        if (!Boolean.TRUE.equals(notification.getIsRead())) {
            readTime = LocalDateTime.now();
            notificationUnreadCacheSupport.markAsRead(
                    userId,
                    notificationId,
                    readTime,
                    () -> notificationMapper.countUnreadByRecipientUserId(userId)
            );
            sendReadSyncMessage(userId, notificationId, readTime);
        }

        NotificationReadVO response = new NotificationReadVO();
        response.setId(notificationId);
        response.setIsRead(Boolean.TRUE);
        response.setReadTime(readTime);
        return response;
    }

    private void sendReadSyncMessage(Long userId, Long notificationId, LocalDateTime readTime) {
        try {
            notificationReadMessageProducer.send(NotificationReadSyncCommand.builder()
                    .userId(userId)
                    .notificationId(notificationId)
                    .readTime(readTime)
                    .build());
        } catch (Exception ex) {
            log.warn("发送通知已读回写 MQ 消息失败，将由定时任务兜底，userId={}, notificationId={}",
                    userId,
                    notificationId,
                    ex);
        }
    }
}
