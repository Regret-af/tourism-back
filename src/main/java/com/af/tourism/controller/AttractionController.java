package com.af.tourism.controller;

import cn.hutool.json.JSONUtil;
import com.af.tourism.common.ApiResponse;
import com.af.tourism.common.ErrorCode;
import com.af.tourism.pojo.dto.AttractionQueryDTO;
import com.af.tourism.pojo.vo.AttractionCardVO;
import com.af.tourism.pojo.vo.PageResponse;
import com.af.tourism.service.AttractionService;
import com.af.tourism.utils.ListCacheUtils;
import com.af.tourism.utils.RedisUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * 景点推荐列表接口
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
public class AttractionController {

    private final AttractionService attractionService;
    private final RedisUtils redisUtils;

    public AttractionController(AttractionService attractionService, RedisUtils redisUtils) {
        this.attractionService = attractionService;
        this.redisUtils = redisUtils;
    }

    /**
     * 按条件查找景点信息
     * @param queryDTO 查询参数
     * @return 景点信息列表
     */
    @GetMapping("/attractions")
    public ApiResponse<PageResponse<AttractionCardVO>> listAttractions(AttractionQueryDTO queryDTO) {

        log.info("开始查询景点信息:{}", queryDTO);

        // 1. 进行参数校验
        if (queryDTO.getPage() < 1 || queryDTO.getSize() < 1) {
            return ApiResponse.fail(ErrorCode.PARAM_INVALID, "参数有误");
        }

        // 2. 尝试获取缓存
        String key = ListCacheUtils.buildHashKey("attractions", queryDTO);
        String cacheData = redisUtils.get(key);
        if (cacheData != null) {
            PageResponse<AttractionCardVO> data = ListCacheUtils.toBean(cacheData, new TypeReference<PageResponse<AttractionCardVO>>() {});
            return ApiResponse.ok(data);
        }

        // 3. 进行查询
        PageResponse<AttractionCardVO> data = attractionService.listAttractions(
                queryDTO.getPage(),
                queryDTO.getSize(),
                queryDTO.getQ(),
                queryDTO.getLocation(),
                queryDTO.getPriceLevel(),
                queryDTO.getTags(),
                queryDTO.getSort());

        // 4. 存入缓存
        String json = ListCacheUtils.toJSON(data);
        redisUtils.setEx(key, json, 120, TimeUnit.SECONDS);

        // 5. 返回数据
        return ApiResponse.ok(data);
    }
}
