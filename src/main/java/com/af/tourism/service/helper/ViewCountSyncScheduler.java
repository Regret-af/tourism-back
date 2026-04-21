package com.af.tourism.service.helper;

import com.af.tourism.mapper.AttractionMapper;
import com.af.tourism.mapper.DiaryMapper;
import com.af.tourism.service.cache.CacheCounterSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ViewCountSyncScheduler {

    private final CacheCounterSupport cacheCounterSupport;
    private final DiaryMapper diaryMapper;
    private final AttractionMapper attractionMapper;

    /**
     * 定时将浏览量回写数据库
     */
    @Scheduled(initialDelay = 60000, fixedDelay = 60000)
    public void syncViewCounts() {
        syncDiaryViewCounts();
        syncAttractionViewCounts();
    }

    /**
     * 同步日记浏览量
     */
    private void syncDiaryViewCounts() {
        for (String key : cacheCounterSupport.listDiaryViewDeltaKeys()) {
            syncDiaryViewDelta(key);
        }
    }

    /**
     * 同步景点浏览量
     */
    private void syncAttractionViewCounts() {
        for (String key : cacheCounterSupport.listAttractionViewDeltaKeys()) {
            syncAttractionViewDelta(key);
        }
    }

    /**
     * 同步日记浏览量增量
     * @param key redis key
     */
    private void syncDiaryViewDelta(String key) {
        Long delta = cacheCounterSupport.getPendingViewDelta(key);
        if (delta == null || delta <= 0) {
            cacheCounterSupport.clearPendingViewDelta(key);
            return;
        }

        Long diaryId = parseEntityId(key);
        int rows = diaryMapper.increaseViewCountByDelta(diaryId, delta);
        if (rows <= 0) {
            log.warn("回写日记浏览量失败，日记不存在，diaryId={}, key={}", diaryId, key);
            cacheCounterSupport.clearPendingViewDelta(key);
            return;
        }

        clearFlushedDelta(key, delta);
    }

    /**
     * 同步景点浏览量增量
     * @param key redis key
     */
    private void syncAttractionViewDelta(String key) {
        Long delta = cacheCounterSupport.getPendingViewDelta(key);
        if (delta == null || delta <= 0) {
            cacheCounterSupport.clearPendingViewDelta(key);
            return;
        }

        Long attractionId = parseEntityId(key);
        int rows = attractionMapper.increaseViewCountByDelta(attractionId, delta);
        if (rows <= 0) {
            log.warn("回写景点浏览量失败，景点不存在，attractionId={}, key={}", attractionId, key);
            cacheCounterSupport.clearPendingViewDelta(key);
            return;
        }

        clearFlushedDelta(key, delta);
    }

    /**
     * 清理刷新增量
     * @param key redis key
     * @param delta 增量
     */
    private void clearFlushedDelta(String key, long delta) {
        Long remain = cacheCounterSupport.consumePendingViewDelta(key, delta);
        if (remain != null && remain <= 0) {
            cacheCounterSupport.clearPendingViewDelta(key);
        }
    }

    /**
     * 解析 id
     * @param key Redis 键
     * @return
     */
    private Long parseEntityId(String key) {
        int lastColonIndex = key.lastIndexOf(':');
        if (lastColonIndex < 0 || lastColonIndex >= key.length() - 1) {
            throw new IllegalStateException("无法解析浏览量增量 key: " + key);
        }
        return Long.valueOf(key.substring(lastColonIndex + 1));
    }
}
