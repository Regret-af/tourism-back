package com.af.tourism.service.cache;

import com.af.tourism.common.constants.RedisKeyConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CacheClearSupport {

    private final CacheClient cacheClient;
    private final CacheKeySupport cacheKeySupport;
    private final CacheKeyBuilder cacheKeyBuilder;

    /**
     * 清理景点类别缓存
     */
    public void clearAttractionCategoryList() {
        String cacheKey = cacheKeySupport.buildAttractionCategoryListKey();

        try {
            cacheClient.delete(cacheKey);
        } catch (Exception ex) {
            log.warn("删除景点分类缓存失败，cacheKey={}", cacheKey, ex);
        }
    }

    /**
     * 清理景点列表缓存
     */
    public void clearAttractionList() {
        String cacheKeyPattern = buildPattern(RedisKeyConstants.ATTRACTION_LIST);

        try {
            cacheClient.deleteByPattern(cacheKeyPattern);
        } catch (Exception ex) {
            log.warn("删除景点列表缓存失败，cacheKeyPattern={}", cacheKeyPattern, ex);
        }
    }

    /**
     * 清理单一景点详情缓存
     * @param attractionId 景点 id
     */
    public void clearAttractionDetail(Long attractionId) {
        String cacheKey = cacheKeySupport.buildAttractionDetailKey(attractionId);

        try {
            cacheClient.delete(cacheKey);
        } catch (Exception ex) {
            log.warn("删除景点详情缓存失败，cacheKey={}", cacheKey, ex);
        }
    }

    /**
     * 清理全部景点详情缓存
     */
    public void clearAllAttractionDetail() {
        String cacheKeyPattern = buildPattern(RedisKeyConstants.ATTRACTION_DETAIL);

        try {
            cacheClient.deleteByPattern(cacheKeyPattern);
        } catch (Exception ex) {
            log.warn("删除景点详情缓存失败，cacheKeyPattern={}", cacheKeyPattern, ex);
        }
    }

    /**
     * 清理景点天气缓存
     * @param attractionId 景点 id
     */
    public void clearAttractionWeather(Long attractionId) {
        String cacheKey = cacheKeySupport.buildAttractionWeatherKey(attractionId);

        try {
            cacheClient.delete(cacheKey);
        } catch (Exception ex) {
            log.warn("删除景点天气缓存失败，cacheKey={}", cacheKey, ex);
        }
    }

    /**
     * 清理日记列表缓存
     */
    public void clearDiaryList() {
        String cacheKeyPattern = buildPattern(RedisKeyConstants.DIARY_LIST);

        try {
            cacheClient.deleteByPattern(cacheKeyPattern);
        } catch (Exception ex) {
            log.warn("删除日记列表缓存失败，cacheKeyPattern={}", cacheKeyPattern, ex);
        }
    }

    /**
     * 清理日记详情缓存
     * @param diaryId 日记 id
     */
    public void clearDiaryDetail(Long diaryId) {
        String cacheKeyPattern = buildPattern(RedisKeyConstants.DIARY_DETAIL, "diaryId", diaryId);

        try {
            cacheClient.deleteByPattern(cacheKeyPattern);
        } catch (Exception ex) {
            log.warn("删除日记详情缓存失败，cacheKeyPattern={}", cacheKeyPattern, ex);
        }
    }

    /**
     * 清理日记评论列表缓存
     * @param diaryId 日记 id
     */
    public void clearDiaryCommentList(Long diaryId) {
        String cacheKeyPattern = buildPattern(RedisKeyConstants.DIARY_COMMENT_LIST, "diaryId", diaryId);

        try {
            cacheClient.deleteByPattern(cacheKeyPattern);
        } catch (Exception ex) {
            log.warn("删除日记评论列表缓存失败，cacheKeyPattern={}", cacheKeyPattern, ex);
        }
    }

    /**
     * 清理我的日记列表缓存
     * @param userId 用户 id
     */
    public void clearMyDiaryList(Long userId) {
        String cacheKeyPattern = buildPattern(RedisKeyConstants.DIARY_MY_LIST, "userId", userId);

        try {
            cacheClient.deleteByPattern(cacheKeyPattern);
        } catch (Exception ex) {
            log.warn("删除我的日记列表缓存失败，cacheKeyPattern={}", cacheKeyPattern, ex);
        }
    }

    /**
     * 清理用户公开日记列表缓存
     * @param userId 用户 id
     */
    public void clearUserPublicDiaryList(Long userId) {
        String cacheKeyPattern = buildPattern(RedisKeyConstants.DIARY_USER_PUBLIC_LIST, "userId", userId);

        try {
            cacheClient.deleteByPattern(cacheKeyPattern);
        } catch (Exception ex) {
            log.warn("删除用户主页日记列表缓存失败，cacheKeyPattern={}", cacheKeyPattern, ex);
        }
    }

    /**
     * 清理作者更多日记列表缓存
     * @param userId 用户 id
     */
    public void clearMoreFromAuthor(Long userId) {
        String cacheKeyPattern = buildPattern(RedisKeyConstants.DIARY_MORE_FROM_AUTHOR, "userId", userId);

        try {
            cacheClient.deleteByPattern(cacheKeyPattern);
        } catch (Exception ex) {
            log.warn("删除更多作者作品缓存失败，cacheKeyPattern={}", cacheKeyPattern, ex);
        }
    }

    /**
     * 构建模式串
     * @param prefix 前缀
     * @param parts 组成部分
     * @return 模式串
     */
    private String buildPattern(String prefix, Object... parts) {
        return cacheKeyBuilder.build(prefix, parts) + "*";
    }
}
