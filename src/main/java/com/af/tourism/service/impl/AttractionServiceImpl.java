package com.af.tourism.service.impl;

import cn.hutool.core.util.StrUtil;
import com.af.tourism.mapper.AttractionMapper;
import com.af.tourism.pojo.entity.Attraction;
import com.af.tourism.pojo.vo.AttractionCardVO;
import com.af.tourism.pojo.vo.PageResponse;
import com.af.tourism.converter.AttractionConverter;
import com.af.tourism.service.AttractionService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 景点服务实现
 */
@Service
public class AttractionServiceImpl implements AttractionService {

    private final AttractionMapper attractionMapper;
    private final AttractionConverter attractionConverter;

    public AttractionServiceImpl(AttractionMapper attractionMapper, AttractionConverter attractionConverter) {
        this.attractionMapper = attractionMapper;
        this.attractionConverter = attractionConverter;
    }

    @Override
    public PageResponse<AttractionCardVO> listAttractions(Integer page, Integer size, String q, String location, Integer priceLevel, String tags, String sort) {
        int pageNum = page;
        int pageSize = size;

        List<String> tagList = attractionConverter.parseTags(tags);
        SortSpec sortSpec = buildSort(sort);

        PageHelper.startPage(pageNum, pageSize);
        List<Attraction> attractions = attractionMapper.selectAttractions(
                q, location, priceLevel, tagList, sortSpec.column, sortSpec.order);
        PageInfo<Attraction> pageInfo = new PageInfo<>(attractions);

        List<AttractionCardVO> voList = attractionConverter.toCardVOList(attractions);

        PageResponse<AttractionCardVO> resp = new PageResponse<>();
        resp.setList(voList);
        resp.setPage(pageInfo.getPageNum());
        resp.setSize(pageInfo.getPageSize());
        resp.setTotal(pageInfo.getTotal());
        resp.setHasNext(pageInfo.isHasNextPage());
        return resp;
    }

    private SortSpec buildSort(String sort) {
        String sortField = StrUtil.nullToDefault(sort, "-createdAt");
        boolean desc = sortField.startsWith("-");
        if (desc) {
            sortField = sortField.substring(1);
        }
        String column;
        switch (sortField) {
            case "viewCount":
                column = "view_count";
                break;
            case "rating":
                column = "rating";
                break;
            case "priceLevel":
                column = "price_level";
                break;
            default:
                column = "created_at";
        }
        return new SortSpec(column, desc ? "DESC" : "ASC");
    }

    private static class SortSpec {
        final String column;
        final String order;

        SortSpec(String column, String order) {
            this.column = column;
            this.order = order;
        }
    }
}
