package com.af.tourism.service.impl;

import cn.hutool.core.util.StrUtil;
import com.af.tourism.converter.DiaryConverter;
import com.af.tourism.mapper.DiaryMapper;
import com.af.tourism.pojo.entity.DiaryWithUser;
import com.af.tourism.pojo.vo.DiaryCardVO;
import com.af.tourism.pojo.vo.PageResponse;
import com.af.tourism.service.DiaryService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 日记列表服务
 */
@Service
public class DiaryServiceImpl implements DiaryService {

    private final DiaryMapper diaryMapper;
    private final DiaryConverter diaryConverter;

    public DiaryServiceImpl(DiaryMapper diaryMapper, DiaryConverter diaryConverter) {
        this.diaryMapper = diaryMapper;
        this.diaryConverter = diaryConverter;
    }

    @Override
    public PageResponse<DiaryCardVO> listDiaries(Integer page, Integer size, Long userId, Integer featured, String q, String sort) {
        SortSpec sortSpec = buildSort(sort);

        PageHelper.startPage(page, size);
        List<DiaryWithUser> list = diaryMapper.selectDiaryList(userId, featured, q, sortSpec.column, sortSpec.order);
        PageInfo<DiaryWithUser> pageInfo = new PageInfo<>(list);

        List<DiaryCardVO> voList = diaryConverter.toCardVOList(list);

        PageResponse<DiaryCardVO> resp = new PageResponse<>();
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
            case "likeCount":
                column = "d.like_count";
                break;
            case "viewCount":
                column = "d.view_count";
                break;
            default:
                column = "d.created_at";
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

