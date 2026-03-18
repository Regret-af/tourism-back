package com.af.tourism.service.impl;

import com.af.tourism.common.ApiResponse;
import com.af.tourism.mapper.AttractionMapper;
import com.af.tourism.pojo.dto.AttractionQueryDTO;
import com.af.tourism.pojo.entity.Attraction;
import com.af.tourism.pojo.vo.AttractionCardVO;
import com.af.tourism.pojo.vo.PageResponse;
import com.af.tourism.converter.AttractionConverter;
import com.af.tourism.service.AttractionService;
import com.af.tourism.utils.ListCacheUtils;
import com.af.tourism.utils.RedisUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 景点服务实现
 */
@Service
public class AttractionServiceImpl implements AttractionService {

    private final AttractionMapper attractionMapper;
    private final AttractionConverter attractionConverter;
    private final RedisUtils redisUtils;

    public AttractionServiceImpl(AttractionMapper attractionMapper, AttractionConverter attractionConverter, RedisUtils redisUtils) {
        this.attractionMapper = attractionMapper;
        this.attractionConverter = attractionConverter;
        this.redisUtils = redisUtils;
    }

    @Override
    public PageResponse<AttractionCardVO> listAttractions(AttractionQueryDTO queryDTO) {

        // 1.尝试获取缓存
        String key = ListCacheUtils.buildHashKey("attractions", queryDTO);
        String cacheData = redisUtils.get(key);
        if (cacheData != null) {
            return ListCacheUtils.toBean(cacheData, new TypeReference<PageResponse<AttractionCardVO>>() {});
        }

        // 没有命中缓存，查询数据库
        // 2.执行参数解析
        queryDTO.parseParams();

        // 3.开始分页查询
        PageHelper.startPage(queryDTO.getPage(), queryDTO.getSize());

        List<Attraction> attractions = attractionMapper.selectAttractions(queryDTO);
        PageInfo<Attraction> pageInfo = new PageInfo<>(attractions);

        // 4.将景点实体转为 VO 卡片
        List<AttractionCardVO> voList = attractionConverter.toCardVOList(attractions);

        // 5.封装为分页返回类型
        PageResponse<AttractionCardVO> resp = new PageResponse<>();
        resp.setList(voList);
        resp.setPage(pageInfo.getPageNum());
        resp.setSize(pageInfo.getPageSize());
        resp.setTotal(pageInfo.getTotal());
        resp.setHasNext(pageInfo.isHasNextPage());

        // 6.存入缓存
        String json = ListCacheUtils.toJSON(resp);
        redisUtils.setEx(key, json, 120, TimeUnit.SECONDS);

        return resp;
    }

}
