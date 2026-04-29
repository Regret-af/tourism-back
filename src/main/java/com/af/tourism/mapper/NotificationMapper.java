package com.af.tourism.mapper;

import com.af.tourism.pojo.dto.app.NotificationQueryDTO;
import com.af.tourism.pojo.entity.Notification;
import com.af.tourism.pojo.vo.app.NotificationVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知 Mapper。
 */
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {

    /**
     * 查询用户通知列表
     * @param recipientUserId 用户 id
     * @param queryDTO 查询参数
     * @return 用户通知列表
     */
    List<NotificationVO> selectNotificationPage(@Param("recipientUserId") Long recipientUserId,
                                                @Param("queryDTO") NotificationQueryDTO queryDTO);

    /**
     * 获取用户未读通知数量
     * @param recipientUserId 用户 id
     * @return 未读通知数量
     */
    Long countUnreadByRecipientUserId(@Param("recipientUserId") Long recipientUserId);

    /**
     * 标记通知已读
     * @param notificationId 通知 id
     * @param recipientUserId 用户 id
     * @param readTime 已读时间
     * @return 影响数据条数
     */
    int markAsRead(@Param("notificationId") Long notificationId,
                   @Param("recipientUserId") Long recipientUserId,
                   @Param("readTime") LocalDateTime readTime);

    int markAllAsRead(@Param("recipientUserId") Long recipientUserId,
                      @Param("readTime") LocalDateTime readTime);
}
