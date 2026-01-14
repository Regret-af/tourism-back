package com.af.tourism.service.impl;

import com.af.tourism.converter.DiaryConverter;
import com.af.tourism.mapper.DiaryMapper;
import com.af.tourism.pojo.dto.DiaryQueryDTO;
import com.af.tourism.pojo.entity.DiaryWithUser;
import com.af.tourism.pojo.vo.DiaryCardVO;
import com.af.tourism.pojo.vo.PageResponse;
import com.af.tourism.service.DiaryService;
import com.af.tourism.utils.ListCacheUtils;
import com.af.tourism.utils.RedisUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 日记列表服务
 */
@Service
public class DiaryServiceImpl implements DiaryService {

    private final DiaryMapper diaryMapper;
    private final DiaryConverter diaryConverter;
    private final RedisUtils redisUtils;

    public DiaryServiceImpl(DiaryMapper diaryMapper, DiaryConverter diaryConverter, RedisUtils redisUtils) {
        this.diaryMapper = diaryMapper;
        this.diaryConverter = diaryConverter;
        this.redisUtils = redisUtils;
    }

    @Override
    public PageResponse<DiaryCardVO> listDiaries(DiaryQueryDTO queryDTO) {

        // 1.尝试获取缓存
        String key = ListCacheUtils.buildHashKey("travel_diaries", queryDTO);
        String cacheData = redisUtils.get(key);
        if (cacheData != null) {
            return ListCacheUtils.toBean(cacheData, new TypeReference<PageResponse<DiaryCardVO>>() {});
        }

        // 没有命中缓存，查询数据库
        // 2.执行参数解析
        queryDTO.parseParams();

        // 3.开始分页查询
        PageHelper.startPage(queryDTO.getPage(), queryDTO.getSize());

        List<DiaryWithUser> list = diaryMapper.selectDiaryList(queryDTO);
        PageInfo<DiaryWithUser> pageInfo = new PageInfo<>(list);

        // 4.将笔记实体转为 VO 卡片
        List<DiaryCardVO> voList = diaryConverter.toCardVOList(list);

        // 5.封装为分页返回类型
        PageResponse<DiaryCardVO> resp = new PageResponse<>();
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

