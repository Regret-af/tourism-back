package com.af.tourism.service.cache;

import com.af.tourism.common.constants.RedisTtlConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationUnreadCacheSupport {

    private final CacheClient cacheClient;
    private final CacheKeySupport cacheKeySupport;

    /**
     * 获取当前未读消息数
     * @param userId 用户 id
     * @param dbLoader 函数式接口
     * @return 未读消息数
     */
    public long getUnreadCount(Long userId, Supplier<Long> dbLoader) {
        // 1.构建缓存 key
        String unreadCountKey = cacheKeySupport.buildNotificationUnreadCountKey(userId);

        // 2.查找缓存，存在直接返回(保底 0)
        try {
            Long cachedUnreadCount = cacheClient.get(unreadCountKey, Long.class);
            if (cachedUnreadCount != null) {
                return Math.max(cachedUnreadCount, 0L);
            }
        } catch (Exception ex) {
            log.warn("读取通知未读数缓存失败，回源数据库，userId={}", userId, ex);
        }

        // 3.获取真实未读数量
        long unreadCount = loadAdjustedUnreadCount(userId, dbLoader);
        // 4.写入缓存
        writeUnreadCount(unreadCountKey, unreadCount);

        return unreadCount;
    }

    /**
     * 插入后增加未读数量
     * @param userId 用户 id
     * @param dbLoader 函数式接口
     */
    public void increaseUnreadCountAfterInsert(Long userId, Supplier<Long> dbLoader) {
        // 1.构建缓存 key
        String unreadCountKey = cacheKeySupport.buildNotificationUnreadCountKey(userId);

        // 2.查找缓存，存在直接 +1 退出函数
        try {
            Long cachedUnreadCount = cacheClient.get(unreadCountKey, Long.class);
            if (cachedUnreadCount != null) {
                cacheClient.set(unreadCountKey, cachedUnreadCount + 1, RedisTtlConstants.NOTIFICATION_UNREAD);
                return;
            }
        } catch (Exception ex) {
            log.warn("更新通知未读数缓存失败，回源数据库，userId={}", userId, ex);
        }

        // 3.获取真实未读数量
        long unreadCount = loadAdjustedUnreadCount(userId, dbLoader);

        // 4.写入缓存
        writeUnreadCount(unreadCountKey, unreadCount);
    }

    public void setUnreadCount(Long userId, long unreadCount) {
        writeUnreadCount(cacheKeySupport.buildNotificationUnreadCountKey(userId), unreadCount);
    }

    /**
     * 标记已读
     * @param userId 用户 id
     * @param notificationId 通知 id
     * @param readTime 已读时间
     * @param dbLoader 函数式接口
     * @return 已读时间
     */
    public LocalDateTime markAsRead(Long userId, Long notificationId, LocalDateTime readTime, Supplier<Long> dbLoader) {
        // 1.构建缓存 key
        String pendingReadKey = cacheKeySupport.buildNotificationReadPendingKey(userId, notificationId);

        // 2.查找缓存，存在直接返回，不存在则双写缓存(已读通知信息与未读通知数)
        try {
            LocalDateTime cachedReadTime = getPendingReadTime(pendingReadKey);
            if (cachedReadTime != null) {
                return cachedReadTime;
            }

            cacheClient.set(pendingReadKey, readTime, RedisTtlConstants.NOTIFICATION_PENDING);

            String unreadCountKey = cacheKeySupport.buildNotificationUnreadCountKey(userId);
            Long cachedUnreadCount = cacheClient.get(unreadCountKey, Long.class);
            if (cachedUnreadCount != null) {
                cacheClient.set(unreadCountKey, Math.max(cachedUnreadCount - 1, 0L), RedisTtlConstants.NOTIFICATION_UNREAD);
                return readTime;
            }
        } catch (Exception ex) {
            log.warn("更新通知已读缓存失败，userId={}, notificationId={}", userId, notificationId, ex);
        }

        // 3.获取真实未读数量
        long unreadCount = loadAdjustedUnreadCount(userId, dbLoader);

        // 4.写入未读数
        writeUnreadCount(cacheKeySupport.buildNotificationUnreadCountKey(userId), unreadCount);

        return readTime;
    }

    /**
     * 获取所有已读待回写通知
     * @return 已读待回写通知集合
     */
    public Set<String> listPendingReadKeys() {
        return cacheClient.keys(cacheKeySupport.buildNotificationReadPendingPattern());
    }

    /**
     * 获取通知已读待回写
     * @param pendingReadKey 缓存 key
     * @return 已读时间
     */
    public LocalDateTime getPendingReadTime(String pendingReadKey) {
        try {
            return cacheClient.get(pendingReadKey, LocalDateTime.class);
        } catch (Exception ex) {
            log.warn("读取通知已读待回写缓存失败，key={}", pendingReadKey, ex);
            return null;
        }
    }

    /**
     * 清理已读待回写通知
     * @param pendingReadKey 缓存 key
     */
    public void clearPendingRead(String pendingReadKey) {
        cacheClient.delete(pendingReadKey);
    }

    /**
     * 真实未读数量
     * @param userId 用户 id
     * @param dbLoader 函数式接口
     * @return 真实未读数量
     */
    private long loadAdjustedUnreadCount(Long userId, Supplier<Long> dbLoader) {
        Long dbUnreadCount = dbLoader.get();
        long unreadCount = dbUnreadCount == null ? 0L : dbUnreadCount;
        long pendingReadCount = countPendingReads(userId);
        return Math.max(unreadCount - pendingReadCount, 0L);
    }

    /**
     * 缓存中已读但是未回写数据库的通知数
     * @param userId 用户 id
     * @return 缓存已读数量
     */
    private long countPendingReads(Long userId) {
        Set<String> pendingReadKeys = cacheClient.keys(cacheKeySupport.buildNotificationReadPendingPattern(userId));
        return pendingReadKeys == null ? 0L : pendingReadKeys.size();
    }

    /**
     * 写入未读数
     * @param unreadCountKey 缓存未读 key
     * @param unreadCount 未读数
     */
    private void writeUnreadCount(String unreadCountKey, long unreadCount) {
        try {
            cacheClient.set(unreadCountKey, Math.max(unreadCount, 0L), RedisTtlConstants.NOTIFICATION_UNREAD);
        } catch (Exception ex) {
            log.warn("写入通知未读数缓存失败，key={}", unreadCountKey, ex);
        }
    }
}
