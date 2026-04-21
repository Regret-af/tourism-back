package com.af.tourism.service.helper;

import com.af.tourism.mapper.NotificationMapper;
import com.af.tourism.service.cache.NotificationUnreadCacheSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationReadSyncScheduler {

    private final NotificationMapper notificationMapper;
    private final NotificationUnreadCacheSupport notificationUnreadCacheSupport;

    /**
     * 定时任务，回写已读待回写通知
     */
    @Scheduled(initialDelay = 60000, fixedDelay = 60000)
    public void syncPendingReads() {
        // 1.获取全部已读待回写通知的 key
        Set<String> pendingReadKeys = notificationUnreadCacheSupport.listPendingReadKeys();
        if (pendingReadKeys == null || pendingReadKeys.isEmpty()) {
            return;
        }

        // 2.挨个进行回写
        for (String pendingReadKey : pendingReadKeys) {
            syncPendingRead(pendingReadKey);
        }
    }

    /**
     * 回写已读待回写通知
     * @param pendingReadKey 缓存 key
     */
    private void syncPendingRead(String pendingReadKey) {
        // 1.解析参数
        Long userId = parseValue(pendingReadKey, "userId");
        Long notificationId = parseValue(pendingReadKey, "notificationId");
        if (userId == null || notificationId == null) {
            notificationUnreadCacheSupport.clearPendingRead(pendingReadKey);
            return;
        }

        // 2.读取缓存信息
        LocalDateTime readTime = notificationUnreadCacheSupport.getPendingReadTime(pendingReadKey);
        if (readTime == null) {
            notificationUnreadCacheSupport.clearPendingRead(pendingReadKey);
            return;
        }

        // 3.回写到数据库
        try {
            notificationMapper.markAsRead(notificationId, userId, readTime);
            notificationUnreadCacheSupport.clearPendingRead(pendingReadKey);
        } catch (Exception ex) {
            log.warn("通知已读状态回写数据库失败，userId={}, notificationId={}", userId, notificationId, ex);
        }
    }

    /**
     * 解析 key 中参数
     * @param key 缓存 key
     * @param fieldName 字段名
     * @return key 中的参数
     */
    private Long parseValue(String key, String fieldName) {
        String[] parts = key.split(":");
        for (int i = 0; i < parts.length - 1; i++) {
            if (fieldName.equals(parts[i])) {
                try {
                    return Long.valueOf(parts[i + 1]);
                } catch (NumberFormatException ex) {
                    return null;
                }
            }
        }
        return null;
    }
}
