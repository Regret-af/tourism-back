package com.af.tourism.service.cache;

import com.af.tourism.common.constants.RedisKeyConstants;
import com.af.tourism.pojo.dto.app.AttractionQueryDTO;
import com.af.tourism.pojo.dto.app.DiaryCommentQueryDTO;
import com.af.tourism.pojo.dto.app.DiaryQueryDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CacheKeySupport {

    private final CacheKeyBuilder cacheKeyBuilder;

    /**
     * 构建景点类别 key
     * @return 景点类别 key
     */
    public String buildAttractionCategoryListKey() {
        return cacheKeyBuilder.build(RedisKeyConstants.ATTRACTION_CATEGORY_LIST);
    }

    /**
     * 构建景点列表 key
     * @param queryDTO 请求参数
     * @return 景点列表 key
     */
    public String buildAttractionListKey(AttractionQueryDTO queryDTO) {
        return cacheKeyBuilder.build(
                RedisKeyConstants.ATTRACTION_LIST,
                "pageNum", queryDTO.getPageNum(),
                "pageSize", queryDTO.getPageSize(),
                "keyword", normalizeKeyPart(queryDTO.getKeyword()),
                "categoryId", queryDTO.getCategoryId() == null ? "_" : queryDTO.getCategoryId(),
                "sort", normalizeKeyPart(queryDTO.getSortCode())
        );
    }

    /**
     * 构建景点详情 key
     * @param attractionId 景点 id
     * @return 景点详情 key
     */
    public String buildAttractionDetailKey(Long attractionId) {
        return cacheKeyBuilder.build(RedisKeyConstants.ATTRACTION_DETAIL, attractionId);
    }

    /**
     * 构建景点天气 key
     * @param attractionId 景点 id
     * @return 景点天气 key
     */
    public String buildAttractionWeatherKey(Long attractionId) {
        return cacheKeyBuilder.build(RedisKeyConstants.ATTRACTION_WEATHER, attractionId);
    }

    public String buildAttractionViewCountKey(Long attractionId) {
        return cacheKeyBuilder.build(RedisKeyConstants.ATTRACTION_VIEW_COUNT, attractionId);
    }

    public String buildAttractionViewDeltaKey(Long attractionId) {
        return cacheKeyBuilder.build(RedisKeyConstants.ATTRACTION_VIEW_DELTA, attractionId);
    }

    public String buildAttractionViewDeltaPattern() {
        return cacheKeyBuilder.build(RedisKeyConstants.ATTRACTION_VIEW_DELTA) + ":*";
    }

    public String buildAuthUserKey(Long userId) {
        return cacheKeyBuilder.build(RedisKeyConstants.AUTH, "user", "userId", userId);
    }

    public String buildAuthRoleCodesKey(Long userId) {
        return cacheKeyBuilder.build(RedisKeyConstants.AUTH, "roleCodes", "userId", userId);
    }

    public String buildNotificationUnreadCountKey(Long userId) {
        return cacheKeyBuilder.build(RedisKeyConstants.NOTIFICATION_UNREAD_COUNT, "userId", userId);
    }

    public String buildNotificationReadPendingKey(Long userId, Long notificationId) {
        return cacheKeyBuilder.build(
                RedisKeyConstants.NOTIFICATION_READ_PENDING,
                "userId", userId,
                "notificationId", notificationId
        );
    }

    public String buildNotificationReadPendingPattern() {
        return cacheKeyBuilder.build(RedisKeyConstants.NOTIFICATION_READ_PENDING) + ":*";
    }

    public String buildNotificationReadPendingPattern(Long userId) {
        return cacheKeyBuilder.build(RedisKeyConstants.NOTIFICATION_READ_PENDING, "userId", userId) + ":*";
    }

    /**
     * 构建日记类别 key
     * @return 日记类别 key
     */
    public String buildDiaryCategoryOptionsKey() {
        return cacheKeyBuilder.build(RedisKeyConstants.DIARY_CATEGORY_OPTIONS);
    }

    /**
     * 构建日记列表 key
     * @param queryDTO 请求参数
     * @return 日记列表 key
     */
    public String buildDiaryListKey(DiaryQueryDTO queryDTO) {
        return cacheKeyBuilder.build(
                RedisKeyConstants.DIARY_LIST,
                "pageNum", queryDTO.getPageNum(),
                "pageSize", queryDTO.getPageSize(),
                "sort", queryDTO.getSortCode() == null ? "_" : queryDTO.getSortCode()
        );
    }

    /**
     * 构建日记详情 key
     * @param diaryId 日记 id
     * @param userId 用户 id
     * @return 日记详情 key
     */
    public String buildDiaryDetailKey(Long diaryId, Long userId) {
        return cacheKeyBuilder.build(
                RedisKeyConstants.DIARY_DETAIL,
                "diaryId", diaryId,
                "userId", userId == null ? "_" : userId
        );
    }

    /**
     * 构建日记数据 key
     * @param diaryId 日记 id
     * @return 日记数据 key
     */
    public String buildDiaryCounterKey(Long diaryId) {
        return cacheKeyBuilder.build(RedisKeyConstants.DIARY_COUNTER, "diaryId", diaryId);
    }

    public String buildDiaryViewDeltaKey(Long diaryId) {
        return cacheKeyBuilder.build(RedisKeyConstants.DIARY_VIEW_DELTA, diaryId);
    }

    public String buildDiaryViewDeltaPattern() {
        return cacheKeyBuilder.build(RedisKeyConstants.DIARY_VIEW_DELTA) + ":*";
    }

    /**
     * 构建我的日记列表 key
     * @param userId 用户 id
     * @param queryDTO 请求参数
     * @return 我的日记列表 key
     */
    public String buildMyDiaryListKey(Long userId, DiaryQueryDTO queryDTO) {
        return cacheKeyBuilder.build(
                RedisKeyConstants.DIARY_MY_LIST,
                "userId", userId,
                "pageNum", queryDTO.getPageNum(),
                "pageSize", queryDTO.getPageSize(),
                "sort", queryDTO.getSortCode() == null ? "_" : queryDTO.getSortCode()
        );
    }

    /**
     * 构建用户公开日记列表 key
     * @param userId 用户 id
     * @param queryDTO 请求参数
     * @return 用户公开日记列表 key
     */
    public String buildUserPublicDiaryListKey(Long userId, DiaryQueryDTO queryDTO) {
        return cacheKeyBuilder.build(
                RedisKeyConstants.DIARY_USER_PUBLIC_LIST,
                "userId", userId,
                "pageNum", queryDTO.getPageNum(),
                "pageSize", queryDTO.getPageSize(),
                "sort", queryDTO.getSortCode() == null ? "_" : queryDTO.getSortCode()
        );
    }

    /**
     * 构建作者更多作品 key
     * @param userId 用户 id
     * @param diaryId 日记 id
     * @param queryDTO 请求参数
     * @return 作者更多作品 key
     */
    public String buildMoreFromAuthorKey(Long userId, Long diaryId, DiaryQueryDTO queryDTO) {
        return cacheKeyBuilder.build(
                RedisKeyConstants.DIARY_MORE_FROM_AUTHOR,
                "userId", userId,
                "diaryId", diaryId,
                "pageNum", queryDTO.getPageNum(),
                "pageSize", queryDTO.getPageSize(),
                "sort", queryDTO.getSortCode() == null ? "_" : queryDTO.getSortCode()
        );
    }

    /**
     * 构建日记评论列表 key
     * @param diaryId 日记 id
     * @param queryDTO 请求参数
     * @return 日记评论列表 key
     */
    public String buildDiaryCommentListKey(Long diaryId, DiaryCommentQueryDTO queryDTO) {
        return cacheKeyBuilder.build(
                RedisKeyConstants.DIARY_COMMENT_LIST,
                "diaryId", diaryId,
                "pageNum", queryDTO.getPageNum(),
                "pageSize", queryDTO.getPageSize()
        );
    }

    /**
     * 规范化 key 组成
     * @param value key 组成部分
     * @return 为空返回 _，不为空直接返回
     */
    private String normalizeKeyPart(String value) {
        return StringUtils.defaultIfBlank(value, "_").trim();
    }
}
