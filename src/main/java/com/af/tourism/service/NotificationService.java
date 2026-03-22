package com.af.tourism.service;

import com.af.tourism.pojo.dto.NotificationQueryDTO;
import com.af.tourism.pojo.vo.NotificationReadVO;
import com.af.tourism.pojo.vo.NotificationUnreadCountVO;
import com.af.tourism.pojo.vo.NotificationVO;
import com.af.tourism.pojo.vo.PageResponse;

public interface NotificationService {

    /**
     * 查询用户通知列表
     * @param userId 用户 id
     * @param queryDTO 查询参数
     * @return 用户通知列表
     */
    PageResponse<NotificationVO> listNotifications(Long userId, NotificationQueryDTO queryDTO);

    /**
     * 获取用户未读通知数量
     * @param userId 用户 id
     * @return 未读通知数量
     */
    NotificationUnreadCountVO getUnreadCount(Long userId);

    /**
     * 标记通知已读
     * @param userId 用户 id
     * @param notificationId 通知 id
     * @return 已读信息
     */
    NotificationReadVO markAsRead(Long userId, Long notificationId);
}
