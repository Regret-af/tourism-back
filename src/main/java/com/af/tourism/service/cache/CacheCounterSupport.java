package com.af.tourism.service.cache;

import com.af.tourism.common.constants.RedisTtlConstants;
import com.af.tourism.pojo.vo.app.AttractionCardVO;
import com.af.tourism.pojo.vo.app.AttractionDetailVO;
import com.af.tourism.pojo.vo.app.DiaryCardVO;
import com.af.tourism.pojo.vo.app.DiaryDetailVO;
import com.af.tourism.pojo.vo.app.DiaryProfileCardVO;
import com.af.tourism.pojo.vo.app.MyDiaryProfileCardVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class CacheCounterSupport {

    private static final String VIEW_COUNT = "viewCount";
    private static final String LIKE_COUNT = "likeCount";
    private static final String FAVORITE_COUNT = "favoriteCount";
    private static final String COMMENT_COUNT = "commentCount";

    private final CacheClient cacheClient;
    private final CacheKeySupport cacheKeySupport;

    /**
     * 初始化日记计数缓存。已存在时不覆盖，避免 Redis 中较新的实时计数被数据库旧值回退。
     * @param diaryId 日记 id
     * @param viewCount 浏览量
     * @param likeCount 点赞量
     * @param favoriteCount 收藏量
     * @param commentCount 评论量
     */
    public void syncDiaryCounters(Long diaryId, Integer viewCount, Integer likeCount, Integer favoriteCount, Integer commentCount) {
        // 已存在时不覆盖，避免把 Redis 中的实时计数回退成数据库旧值
        if (hasDiaryCounters(diaryId)) {
            return;
        }

        // 1.构建日记数据 key
        String cacheKey = cacheKeySupport.buildDiaryCounterKey(diaryId);

        // 2.将数据统计放入 Hash 表
        Map<String, Object> values = new LinkedHashMap<>(4);
        values.put(VIEW_COUNT, defaultCount(viewCount));
        values.put(LIKE_COUNT, defaultCount(likeCount));
        values.put(FAVORITE_COUNT, defaultCount(favoriteCount));
        values.put(COMMENT_COUNT, defaultCount(commentCount));

        // 3.将数据统计存入缓存并设置过期时间
        cacheClient.putAllHash(cacheKey, values);
        cacheClient.expire(cacheKey, RedisTtlConstants.DEFAULT);
    }

    /**
     * 增加日记浏览量，并记录待回写数据库的浏览量增量。
     * @param diaryId 日记 id
     * @param delta 增量
     */
    public void incrementDiaryViewCount(Long diaryId, long delta) {
        incrementDiaryCounter(diaryId, VIEW_COUNT, delta);
        incrementViewDelta(cacheKeySupport.buildDiaryViewDeltaKey(diaryId), delta);
    }

    /**
     * 增加日记点赞量。
     * @param diaryId 日记 id
     * @param delta 增量
     */
    public void incrementDiaryLikeCount(Long diaryId, long delta) {
        incrementDiaryCounter(diaryId, LIKE_COUNT, delta);
    }

    /**
     * 增加日记收藏量。
     * @param diaryId 日记 id
     * @param delta 增量
     */
    public void incrementDiaryFavoriteCount(Long diaryId, long delta) {
        incrementDiaryCounter(diaryId, FAVORITE_COUNT, delta);
    }

    /**
     * 增加日记评论量。
     * @param diaryId 日记 id
     * @param delta 增量
     */
    public void incrementDiaryCommentCount(Long diaryId, long delta) {
        incrementDiaryCounter(diaryId, COMMENT_COUNT, delta);
    }

    /**
     * 使用 Redis 中的实时计数覆盖日记详情中的计数字段。
     * @param detailVO 日记详情
     * @param diaryId 日记 id
     */
    public void fillDiaryCounters(DiaryDetailVO detailVO, Long diaryId) {
        // 1.参数校验
        if (detailVO == null) {
            return;
        }

        Map<Object, Object> entries = getDiaryCounterEntries(diaryId);
        if (entries == null || entries.isEmpty()) {
            return;
        }

        detailVO.setViewCount(readCount(entries, VIEW_COUNT, detailVO.getViewCount()));
        detailVO.setLikeCount(readCount(entries, LIKE_COUNT, detailVO.getLikeCount()));
        detailVO.setFavoriteCount(readCount(entries, FAVORITE_COUNT, detailVO.getFavoriteCount()));
        detailVO.setCommentCount(readCount(entries, COMMENT_COUNT, detailVO.getCommentCount()));
    }

    /**
     * 为日记卡片列表填充实时计数。
     * @param list 日记卡片列表
     */
    public void fillDiaryCardCounters(List<DiaryCardVO> list) {
        if (list == null || list.isEmpty()) {
            return;
        }

        for (DiaryCardVO item : list) {
            Map<Object, Object> entries = getDiaryCounterEntries(item.getId());
            if (entries == null || entries.isEmpty()) {
                continue;
            }
            item.setViewCount(readCount(entries, VIEW_COUNT, item.getViewCount()));
            item.setLikeCount(readCount(entries, LIKE_COUNT, item.getLikeCount()));
            item.setFavoriteCount(readCount(entries, FAVORITE_COUNT, item.getFavoriteCount()));
            item.setCommentCount(readCount(entries, COMMENT_COUNT, item.getCommentCount()));
        }
    }

    /**
     * 为用户主页日记卡片列表填充实时计数。
     * @param list 用户主页日记卡片列表
     */
    public void fillDiaryProfileCardCounters(List<DiaryProfileCardVO> list) {
        if (list == null || list.isEmpty()) {
            return;
        }

        for (DiaryProfileCardVO item : list) {
            Map<Object, Object> entries = getDiaryCounterEntries(item.getId());
            if (entries == null || entries.isEmpty()) {
                continue;
            }
            item.setViewCount(readCount(entries, VIEW_COUNT, item.getViewCount()));
            item.setLikeCount(readCount(entries, LIKE_COUNT, item.getLikeCount()));
            item.setFavoriteCount(readCount(entries, FAVORITE_COUNT, item.getFavoriteCount()));
            item.setCommentCount(readCount(entries, COMMENT_COUNT, item.getCommentCount()));
        }
    }

    /**
     * 为我的日记卡片列表填充实时计数。
     * @param list 我的日记卡片列表
     */
    public void fillMyDiaryProfileCardCounters(List<MyDiaryProfileCardVO> list) {
        if (list == null || list.isEmpty()) {
            return;
        }

        for (MyDiaryProfileCardVO item : list) {
            Map<Object, Object> entries = getDiaryCounterEntries(item.getId());
            if (entries == null || entries.isEmpty()) {
                continue;
            }
            item.setViewCount(readCount(entries, VIEW_COUNT, item.getViewCount()));
            item.setLikeCount(readCount(entries, LIKE_COUNT, item.getLikeCount()));
            item.setFavoriteCount(readCount(entries, FAVORITE_COUNT, item.getFavoriteCount()));
            item.setCommentCount(readCount(entries, COMMENT_COUNT, item.getCommentCount()));
        }
    }

    /**
     * 初始化景点浏览量缓存。已存在时不覆盖。
     * @param attractionId 景点 id
     * @param viewCount 数据库中的浏览量
     */
    public void syncAttractionViewCount(Long attractionId, Integer viewCount) {
        cacheClient.setIfAbsent(
                cacheKeySupport.buildAttractionViewCountKey(attractionId),
                defaultCount(viewCount),
                RedisTtlConstants.DEFAULT
        );
    }

    /**
     * 增加景点浏览量，并记录待回写数据库的浏览量增量。
     * @param attractionId 景点 id
     * @param delta 增量
     */
    public void incrementAttractionViewCount(Long attractionId, long delta) {
        String viewCountKey = cacheKeySupport.buildAttractionViewCountKey(attractionId);
        Long latestViewCount = cacheClient.increment(viewCountKey, delta);
        if (latestViewCount != null) {
            cacheClient.expire(viewCountKey, RedisTtlConstants.DEFAULT);
        }

        incrementViewDelta(cacheKeySupport.buildAttractionViewDeltaKey(attractionId), delta);
    }

    /**
     * 使用 Redis 中的实时浏览量覆盖景点详情中的浏览量。
     * @param detailVO 景点详情
     * @param attractionId 景点 id
     */
    public void fillAttractionViewCount(AttractionDetailVO detailVO, Long attractionId) {
        if (detailVO == null) {
            return;
        }
        detailVO.setViewCount(readAttractionViewCount(attractionId, detailVO.getViewCount()));
    }

    /**
     * 为景点卡片列表填充实时浏览量。
     * @param list 景点卡片列表
     */
    public void fillAttractionCardViewCounts(List<AttractionCardVO> list) {
        if (list == null || list.isEmpty()) {
            return;
        }

        for (AttractionCardVO item : list) {
            item.setViewCount(readAttractionViewCount(item.getId(), item.getViewCount()));
        }
    }

    /**
     * 获取待回写的日记浏览量增量 key。
     * @return 增量 key 集合
     */
    public Set<String> listPendingDiaryViewDeltaKeys() {
        return cacheClient.keys(cacheKeySupport.buildDiaryViewDeltaPattern());
    }

    /**
     * 获取待回写的景点浏览量增量 key。
     * @return 增量 key 集合
     */
    public Set<String> listPendingAttractionViewDeltaKeys() {
        return cacheClient.keys(cacheKeySupport.buildAttractionViewDeltaPattern());
    }

    /**
     * 读取待回写的浏览量增量。
     * @param key Redis key
     * @return 增量值
     */
    public Long getViewDelta(String key) {
        return cacheClient.get(key, Long.class);
    }

    /**
     * 扣减已经回写成功的浏览量增量。
     * @param key Redis key
     * @param delta 已回写增量
     * @return 剩余增量
     */
    public Long consumeViewDelta(String key, long delta) {
        return cacheClient.increment(key, -delta);
    }

    /**
     * 清理待回写的浏览量增量。
     * @param key Redis key
     */
    public void clearViewDelta(String key) {
        cacheClient.delete(key);
    }

    private void incrementDiaryCounter(Long diaryId, String field, long delta) {
        String cacheKey = cacheKeySupport.buildDiaryCounterKey(diaryId);
        Long latestCount = cacheClient.incrementHash(cacheKey, field, delta);
        if (latestCount != null) {
            cacheClient.expire(cacheKey, RedisTtlConstants.DEFAULT);
        }
    }

    private void incrementViewDelta(String deltaKey, long delta) {
        Long latestDelta = cacheClient.increment(deltaKey, delta);
        if (latestDelta != null) {
            cacheClient.expire(deltaKey, RedisTtlConstants.VIEW_COUNT_DELTA);
        }
    }

    private Map<Object, Object> getDiaryCounterEntries(Long diaryId) {
        return cacheClient.entries(cacheKeySupport.buildDiaryCounterKey(diaryId));
    }

    /**
     * 判断日记计数缓存是否已存在。
     * @param diaryId 日记 id
     * @return 是否存在
     */
    private boolean hasDiaryCounters(Long diaryId) {
        Map<Object, Object> entries = getDiaryCounterEntries(diaryId);
        return entries != null && !entries.isEmpty();
    }

    private Integer readCount(Map<Object, Object> entries, String field, Integer defaultValue) {
        Object value = entries.get(field);
        if (value == null) {
            return defaultCount(defaultValue);
        }
        return Integer.parseInt(String.valueOf(value));
    }

    /**
     * 读取景点浏览量总量缓存。
     * @param attractionId 景点 id
     * @param defaultValue 默认值
     * @return 浏览量
     */
    private Integer readAttractionViewCount(Long attractionId, Integer defaultValue) {
        Long viewCount = cacheClient.get(cacheKeySupport.buildAttractionViewCountKey(attractionId), Long.class);
        return viewCount == null ? defaultCount(defaultValue) : viewCount.intValue();
    }

    private Integer defaultCount(Integer value) {
        return value == null ? 0 : value;
    }
}
