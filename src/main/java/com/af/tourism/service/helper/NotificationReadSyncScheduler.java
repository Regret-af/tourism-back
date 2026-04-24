package com.af.tourism.service.helper;

import com.af.tourism.service.cache.NotificationUnreadCacheSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class NotificationReadSyncScheduler {

    private final NotificationUnreadCacheSupport notificationUnreadCacheSupport;
    private final NotificationReadSyncService notificationReadSyncService;

    /**
     * 定时兜底回写已读通知，防止 MQ 发送或消费异常时 Redis pending 长时间不落库。
     */
    @Scheduled(initialDelay = 60000, fixedDelay = 60000)
    public void syncPendingReads() {
        Set<String> pendingReadKeys = notificationUnreadCacheSupport.listPendingReadKeys();
        if (pendingReadKeys == null || pendingReadKeys.isEmpty()) {
            return;
        }

        for (String pendingReadKey : pendingReadKeys) {
            notificationReadSyncService.syncPendingRead(pendingReadKey);
        }
    }
}
