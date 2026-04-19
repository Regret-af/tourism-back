package com.af.tourism.service.impl.app;

import com.af.tourism.common.constants.RedisKeyConstants;
import com.af.tourism.common.constants.RedisTtlConstants;
import com.af.tourism.converter.AttractionConverter;
import com.af.tourism.mapper.AttractionCategoryMapper;
import com.af.tourism.pojo.entity.AttractionCategory;
import com.af.tourism.pojo.vo.app.AttractionCategoryVO;
import com.af.tourism.service.app.AttractionCategoryService;
import com.af.tourism.service.cache.CacheClient;
import com.af.tourism.service.cache.CacheKeyBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttractionCategoryServiceImpl implements AttractionCategoryService {

    private static final TypeReference<List<AttractionCategoryVO>> CATEGORY_LIST_TYPE =
            new TypeReference<List<AttractionCategoryVO>>() {
            };

    private final AttractionCategoryMapper attractionCategoryMapper;

    private final AttractionConverter attractionConverter;

    private final CacheClient cacheClient;
    private final CacheKeyBuilder cacheKeyBuilder;

    /**
     * 景点分类列表
     * @return 景点分类列表项
     */
    @Override
    public List<AttractionCategoryVO> listCategories() {
        // 1.构建 Redis 中景点分类的 key
        String cacheKey = cacheKeyBuilder.build(RedisKeyConstants.ATTRACTION_CATEGORY_LIST);

        // 2.查找 Redis 缓存，存在直接返回
        try {
            List<AttractionCategoryVO> cachedList = cacheClient.get(cacheKey, CATEGORY_LIST_TYPE);
            if (cachedList != null) {
                return cachedList;
            }
        } catch (Exception ex) {
            log.warn("读取景点分类缓存失败，回源数据库，cacheKey={}", cacheKey, ex);
        }

        // 3.查询状态为启用的景点分类列表
        List<AttractionCategory> categories = attractionCategoryMapper.selectEnabledCategories();

        // 4.检查是否为空
        if (categories == null || categories.isEmpty()) {
            log.debug("查询景点分类结果为空");
            return Collections.emptyList();
        }

        // 5.使用转换器进行字段映射
        List<AttractionCategoryVO> response = attractionConverter.toAttractionCategoryVOList(categories);

        // 6.将返回值存入Redis
        try {
            cacheClient.set(cacheKey, response, RedisTtlConstants.CATEGORY);
        } catch (Exception ex) {
            log.warn("写入景点分类缓存失败，cacheKey={}", cacheKey, ex);
        }

        return response;
    }
}
