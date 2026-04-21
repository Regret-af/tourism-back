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
     * 将数据统计同步到缓存
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
     * 为日记浏览量增加指定步长，同时记录待回写的增量。
     * @param diaryId 日记 id
     * @param delta 步长
     */
    public void incrementDiaryViewCount(Long diaryId, long delta) {
        incrementDiaryCounter(diaryId, VIEW_COUNT, delta);
        incrementViewDelta(cacheKeySupport.buildDiaryViewDeltaKey(diaryId), delta);
    }

    /**
     * 为日记点赞量增加指定步长
     * @param diaryId 日记 id
     * @param delta 步长
     */
    public void incrementDiaryLikeCount(Long diaryId, long delta) {
        incrementDiaryCounter(diaryId, LIKE_COUNT, delta);
    }

    /**
     * 为日记收藏量增加指定步长
     * @param diaryId 日记 id
     * @param delta 步长
     */
    public void incrementDiaryFavoriteCount(Long diaryId, long delta) {
        incrementDiaryCounter(diaryId, FAVORITE_COUNT, delta);
    }

    /**
     * 为日记评论量增加指定步长
     * @param diaryId 日记 id
     * @param delta 步长
     */
    public void incrementDiaryCommentCount(Long diaryId, long delta) {
        incrementDiaryCounter(diaryId, COMMENT_COUNT, delta);
    }

    /**
     * 将 Redis 中的日记计数填充到详情对象。
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
     * 填充日记数据
     * @param list 日记列表
     */
    public void fillDiaryCardCounters(List<DiaryCardVO> list) {
        if (list == null || list.isEmpty()) {
            return;
        }

        for (DiaryCardVO diaryCardVO : list) {
            Map<Object, Object> entries = getDiaryCounterEntries(diaryCardVO.getId());
            if (entries == null || entries.isEmpty()) {
                continue;
            }
            diaryCardVO.setViewCount(readCount(entries, VIEW_COUNT, diaryCardVO.getViewCount()));
            diaryCardVO.setLikeCount(readCount(entries, LIKE_COUNT, diaryCardVO.getLikeCount()));
            diaryCardVO.setFavoriteCount(readCount(entries, FAVORITE_COUNT, diaryCardVO.getFavoriteCount()));
            diaryCardVO.setCommentCount(readCount(entries, COMMENT_COUNT, diaryCardVO.getCommentCount()));
        }
    }

    /**
     * 填充日记数据
     * @param list 日记列表
     */
    public void fillDiaryProfileCardCounters(List<DiaryProfileCardVO> list) {
        if (list == null || list.isEmpty()) {
            return;
        }

        for (DiaryProfileCardVO diaryProfileCardVO : list) {
            Map<Object, Object> entries = getDiaryCounterEntries(diaryProfileCardVO.getId());
            if (entries == null || entries.isEmpty()) {
                continue;
            }
            diaryProfileCardVO.setViewCount(readCount(entries, VIEW_COUNT, diaryProfileCardVO.getViewCount()));
            diaryProfileCardVO.setLikeCount(readCount(entries, LIKE_COUNT, diaryProfileCardVO.getLikeCount()));
            diaryProfileCardVO.setFavoriteCount(readCount(entries, FAVORITE_COUNT, diaryProfileCardVO.getFavoriteCount()));
            diaryProfileCardVO.setCommentCount(readCount(entries, COMMENT_COUNT, diaryProfileCardVO.getCommentCount()));
        }
    }

    /**
     * 填充日记数据
     * @param list 日记列表
     */
    public void fillMyDiaryProfileCardCounters(List<MyDiaryProfileCardVO> list) {
        if (list == null || list.isEmpty()) {
            return;
        }

        for (MyDiaryProfileCardVO myDiaryProfileCardVO : list) {
            Map<Object, Object> entries = getDiaryCounterEntries(myDiaryProfileCardVO.getId());
            if (entries == null || entries.isEmpty()) {
                continue;
            }
            myDiaryProfileCardVO.setViewCount(readCount(entries, VIEW_COUNT, myDiaryProfileCardVO.getViewCount()));
            myDiaryProfileCardVO.setLikeCount(readCount(entries, LIKE_COUNT, myDiaryProfileCardVO.getLikeCount()));
            myDiaryProfileCardVO.setFavoriteCount(readCount(entries, FAVORITE_COUNT, myDiaryProfileCardVO.getFavoriteCount()));
            myDiaryProfileCardVO.setCommentCount(readCount(entries, COMMENT_COUNT, myDiaryProfileCardVO.getCommentCount()));
        }
    }

    /**
     * 初始化景点浏览量总量缓存。
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
     * 为景点浏览量增加指定步长，同时记录待回写的增量。
     * @param attractionId 景点 id
     * @param delta 步长
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
     * 用 Redis 中的景点浏览量总量覆盖详情对象。
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
     * 为景点卡片列表填充浏览量总量。
     * @param list 景点列表
     */
    public void fillAttractionCardViewCounts(List<AttractionCardVO> list) {
        if (list == null || list.isEmpty()) {
            return;
        }

        for (AttractionCardVO attractionCardVO : list) {
            attractionCardVO.setViewCount(readAttractionViewCount(attractionCardVO.getId(), attractionCardVO.getViewCount()));
        }
    }

    /**
     * 获取日记浏览量增量 key 列表。
     * @return 增量 key 集合
     */
    public Set<String> listDiaryViewDeltaKeys() {
        return cacheClient.keys(cacheKeySupport.buildDiaryViewDeltaPattern());
    }

    /**
     * 获取景点浏览量增量 key 列表。
     * @return 增量 key 集合
     */
    public Set<String> listAttractionViewDeltaKeys() {
        return cacheClient.keys(cacheKeySupport.buildAttractionViewDeltaPattern());
    }

    /**
     * 读取待回写的浏览量增量。
     * @param key Redis key
     * @return 增量值
     */
    public Long getPendingViewDelta(String key) {
        return cacheClient.get(key, Long.class);
    }

    /**
     * 扣减已经回写成功的浏览量增量。
     * @param key Redis key
     * @param delta 已回写增量
     * @return 剩余增量
     */
    public Long consumePendingViewDelta(String key, long delta) {
        return cacheClient.increment(key, -delta);
    }

    /**
     * 清理待回写的浏览量增量。
     * @param key Redis key
     */
    public void clearPendingViewDelta(String key) {
        cacheClient.delete(key);
    }

    /**
     * 从 Hash 查询结果中读取指定字段的计数值
     * @param entries Hash 结果
     * @param field 字段名
     * @param defaultValue 默认值
     * @return 读取的计数值
     */
    private Integer readCount(Map<Object, Object> entries, String field, Integer defaultValue) {
        Object value = entries.get(field);
        if (value == null) {
            return defaultCount(defaultValue);
        }
        return Integer.parseInt(String.valueOf(value));
    }

    /**
     * 读取日记计数 Hash。
     * @param diaryId 日记 id
     * @return Hash 数据
     */
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

    /**
     * 获取非 null 的计数值。
     * @param value 原始值
     * @return 非 null 计数值
     */
    private Integer defaultCount(Integer value) {
        return value == null ? 0 : value;
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

    /**
     * 更新日记计数并刷新过期时间。
     * @param diaryId 日记 id
     * @param field 字段名
     * @param delta 步长
     */
    private void incrementDiaryCounter(Long diaryId, String field, long delta) {
        String cacheKey = cacheKeySupport.buildDiaryCounterKey(diaryId);
        Long latestCount = cacheClient.incrementHash(cacheKey, field, delta);
        if (latestCount == null) {
            return;
        }

        cacheClient.expire(cacheKey, RedisTtlConstants.DEFAULT);
    }

    /**
     * 更新待回写增量并刷新过期时间。
     * @param deltaKey 增量 key
     * @param delta 步长
     */
    private void incrementViewDelta(String deltaKey, long delta) {
        Long latestDelta = cacheClient.increment(deltaKey, delta);
        if (latestDelta == null) {
            return;
        }

        cacheClient.expire(deltaKey, RedisTtlConstants.VIEW_COUNT_DELTA);
    }
}
