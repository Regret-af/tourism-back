package com.af.tourism.service.cache;

import com.af.tourism.common.constants.RedisTtlConstants;
import com.af.tourism.pojo.vo.app.DiaryDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

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
     * 将浏览量同步到缓存
     * @param diaryId 日记 id
     * @param viewCount 浏览量
     */
    public void syncDiaryViewCount(Long diaryId, Integer viewCount) {
        // 1.构建日记数据 key
        String cacheKey = cacheKeySupport.buildDiaryCounterKey(diaryId);

        // 2.将数据统计存入缓存并设置过期时间
        cacheClient.putHash(cacheKey, VIEW_COUNT, defaultCount(viewCount));
        cacheClient.expire(cacheKey, RedisTtlConstants.DEFAULT);
    }

    /**
     * 为日记浏览量增加指定步长
     * @param diaryId 日记 id
     * @param delta 步长
     */
    public void incrementDiaryViewCount(Long diaryId, long delta) {
        incrementDiaryCounter(diaryId, VIEW_COUNT, delta);
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
     * 填充日记数据
     * @param detailVO 日记详情实体
     * @param diaryId 日记 id
     */
    public void fillDiaryCounters(DiaryDetailVO detailVO, Long diaryId) {
        // 1.参数校验
        if (detailVO == null) {
            return;
        }

        // 2.构建日记数据 key
        String cacheKey = cacheKeySupport.buildDiaryCounterKey(diaryId);
        // 3.查找缓存并存入实体
        Map<Object, Object> entries = cacheClient.entries(cacheKey);
        if (entries == null || entries.isEmpty()) {
            return;
        }

        detailVO.setViewCount(readCount(entries, VIEW_COUNT, detailVO.getViewCount()));
        detailVO.setLikeCount(readCount(entries, LIKE_COUNT, detailVO.getLikeCount()));
        detailVO.setFavoriteCount(readCount(entries, FAVORITE_COUNT, detailVO.getFavoriteCount()));
        detailVO.setCommentCount(readCount(entries, COMMENT_COUNT, detailVO.getCommentCount()));
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
     * 获取默认计数值
     * @param value 原始值
     * @return 非 null 计数值
     */
    private Integer defaultCount(Integer value) {
        return value == null ? 0 : value;
    }

    /**
     * 更新日记计数（自增/自减）并进行校准
     * @param diaryId
     * @param field
     * @param delta
     */
    private void incrementDiaryCounter(Long diaryId, String field, long delta) {
        String cacheKey = cacheKeySupport.buildDiaryCounterKey(diaryId);
        Long latestCount = cacheClient.incrementHash(cacheKey, field, delta);
        if (latestCount == null) {
            return;
        }

        cacheClient.expire(cacheKey, RedisTtlConstants.DEFAULT);
    }
}
