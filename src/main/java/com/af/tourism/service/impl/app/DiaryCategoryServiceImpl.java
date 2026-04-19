package com.af.tourism.service.impl.app;

import com.af.tourism.common.constants.RedisKeyConstants;
import com.af.tourism.common.constants.RedisTtlConstants;
import com.af.tourism.mapper.DiaryCategoryMapper;
import com.af.tourism.pojo.vo.common.OptionVO;
import com.af.tourism.service.app.DiaryCategoryService;
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
public class DiaryCategoryServiceImpl implements DiaryCategoryService {

    private static final TypeReference<List<OptionVO<Long>>> DIARY_CATEGORY_OPTION_LIST_TYPE =
            new TypeReference<List<OptionVO<Long>>>() {
            };

    private final DiaryCategoryMapper diaryCategoryMapper;

    private final CacheClient cacheClient;
    private final CacheKeyBuilder cacheKeyBuilder;

    /**
     * 日记分类选项
     * @return 日记分类选项列表
     */
    @Override
    public List<OptionVO<Long>> listCategoryOptions() {
        // 1.构建 Redis 中日记分类的 key
        String cacheKey = cacheKeyBuilder.build(RedisKeyConstants.DIARY_CATEGORY_OPTIONS);

        // 2.查找 Redis 缓存，存在直接返回
        try {
            List<OptionVO<Long>> cachedOptions = cacheClient.get(cacheKey, DIARY_CATEGORY_OPTION_LIST_TYPE);
            if (cachedOptions != null) {
                return cachedOptions;
            }
        } catch (Exception ex) {
            log.warn("读取日记分类选项缓存失败，回源数据库，cacheKey={}", cacheKey, ex);
        }

        // 3.查询数据库并校验结果
        List<OptionVO<Long>> options = diaryCategoryMapper.selectDiaryCategoryOptions();
        if (options == null || options.isEmpty()) {
            log.debug("查询日记分类选项结果为空");
            return Collections.emptyList();
        }

        // 4.将结果存入Redis
        try {
            cacheClient.set(cacheKey, options, RedisTtlConstants.CATEGORY);
        } catch (Exception ex) {
            log.warn("写入日记分类选项缓存失败，cacheKey={}", cacheKey, ex);
        }

        return options;
    }
}
