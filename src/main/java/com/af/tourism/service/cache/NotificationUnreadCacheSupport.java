package com.af.tourism.service.cache;

import com.af.tourism.common.constants.RedisTtlConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationUnreadCacheSupport {

    private final CacheClient cacheClient;
    private final CacheKeySupport cacheKeySupport;

    /**
     * 获取用户未读通知数，缓存未命中时回源数据库。
     * @param userId 用户 id
     * @param dbLoader 数据库查询函数
     * @return 未读通知数
     */
    public long getUnreadCount(Long userId, Supplier<Long> dbLoader) {
        String unreadCountKey = cacheKeySupport.buildNotificationUnreadCountKey(userId);

        try {
            Long cachedUnreadCount = cacheClient.get(unreadCountKey, Long.class);
            if (cachedUnreadCount != null) {
                return normalizeUnreadCount(cachedUnreadCount);
            }
        } catch (Exception ex) {
            log.warn("读取通知未读数缓存失败，回源数据库，userId={}", userId, ex);
        }

        long unreadCount = loadUnreadCount(dbLoader);
        writeUnreadCount(unreadCountKey, unreadCount);
        return unreadCount;
    }

    /**
     * 新增通知后同步未读数缓存。
     * @param userId 用户 id
     * @param dbLoader 数据库查询函数
     */
    public void increaseUnreadCountAfterInsert(Long userId, Supplier<Long> dbLoader) {
        String unreadCountKey = cacheKeySupport.buildNotificationUnreadCountKey(userId);

        try {
            Long cachedUnreadCount = cacheClient.get(unreadCountKey, Long.class);
            if (cachedUnreadCount != null) {
                cacheClient.set(unreadCountKey, normalizeUnreadCount(cachedUnreadCount) + 1, RedisTtlConstants.NOTIFICATION_UNREAD);
                return;
            }
        } catch (Exception ex) {
            log.warn("更新通知未读数缓存失败，回源数据库，userId={}", userId, ex);
        }

        long unreadCount = loadUnreadCount(dbLoader);
        writeUnreadCount(unreadCountKey, unreadCount);
    }

    /**
     * 直接设置用户未读通知数。
     * @param userId 用户 id
     * @param unreadCount 未读通知数
     */
    public void setUnreadCount(Long userId, long unreadCount) {
        writeUnreadCount(cacheKeySupport.buildNotificationUnreadCountKey(userId), unreadCount);
    }

    private long loadUnreadCount(Supplier<Long> dbLoader) {
        Long dbUnreadCount = dbLoader.get();
        return normalizeUnreadCount(dbUnreadCount);
    }

    private long normalizeUnreadCount(Long unreadCount) {
        return unreadCount == null ? 0L : Math.max(unreadCount, 0L);
    }

    private void writeUnreadCount(String unreadCountKey, long unreadCount) {
        try {
            cacheClient.set(unreadCountKey, normalizeUnreadCount(unreadCount), RedisTtlConstants.NOTIFICATION_UNREAD);
        } catch (Exception ex) {
            log.warn("写入通知未读数缓存失败，key={}", unreadCountKey, ex);
        }
    }
}
