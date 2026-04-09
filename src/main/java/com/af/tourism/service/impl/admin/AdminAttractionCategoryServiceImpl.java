package com.af.tourism.service.impl.admin;

import com.af.tourism.common.ErrorCode;
import com.af.tourism.exception.BusinessException;
import com.af.tourism.mapper.AttractionCategoryMapper;
import com.af.tourism.pojo.dto.admin.AttractionCategoryQueryDTO;
import com.af.tourism.pojo.vo.admin.AttractionCategoryForAdminVO;
import com.af.tourism.pojo.vo.admin.AttractionCategoryStatsForAdminVO;
import com.af.tourism.pojo.vo.common.PageResponse;
import com.af.tourism.service.admin.AdminAttractionCategoryService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 管理端景点分类服务实现
 */
@Service
@RequiredArgsConstructor
public class AdminAttractionCategoryServiceImpl implements AdminAttractionCategoryService {

    private final AttractionCategoryMapper attractionCategoryMapper;

    /**
     * 获取管理端景点分类列表
     * @param queryDTO 查询参数
     * @return 分类分页列表
     */
    @Override
    public PageResponse<AttractionCategoryForAdminVO> listCategories(AttractionCategoryQueryDTO queryDTO) {
        // 1.开启分页查询
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 2.查询景点分类列表
        List<AttractionCategoryForAdminVO> list = attractionCategoryMapper.selectAdminCategoryList(queryDTO);
        PageInfo<AttractionCategoryForAdminVO> pageInfo = new PageInfo<>(list);

        // 3.填充统计字段
        fillAttractionCount(list);

        // 4.构建返回值
        PageResponse<AttractionCategoryForAdminVO> response = new PageResponse<>();
        response.setList(list);
        response.setPageNum(pageInfo.getPageNum());
        response.setPageSize(pageInfo.getPageSize());
        response.setTotal(pageInfo.getTotal());

        return response;
    }

    /**
     * 获取管理端景点分类详情
     * @param id 分类 id
     * @return 分类详情
     */
    @Override
    public AttractionCategoryForAdminVO getCategoryDetail(Long id) {
        // 1.查询景点分类详情
        AttractionCategoryForAdminVO detail = attractionCategoryMapper.selectAdminCategoryDetailById(id);
        if (detail == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "景点分类不存在");
        }

        // 2.填充统计字段
        fillAttractionCount(Collections.singletonList(detail));
        return detail;
    }

    /**
     * 填充景点数量统计
     * @param list 分类列表
     */
    private void fillAttractionCount(List<AttractionCategoryForAdminVO> list) {
        if (list == null || list.isEmpty()) {
            return;
        }

        List<Long> categoryIds = list.stream()
                .map(AttractionCategoryForAdminVO::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (categoryIds.isEmpty()) {
            return;
        }

        List<AttractionCategoryStatsForAdminVO> statsList =
                attractionCategoryMapper.selectAttractionStatsByCategoryIds(categoryIds);

        Map<Long, Long> statsMap = (statsList == null ? Collections.<AttractionCategoryStatsForAdminVO>emptyList() : statsList)
                .stream()
                .collect(Collectors.toMap(
                        AttractionCategoryStatsForAdminVO::getCategoryId,
                        item -> item.getAttractionCount() == null ? 0L : item.getAttractionCount(),
                        (left, right) -> right
                ));

        for (AttractionCategoryForAdminVO item : list) {
            item.setAttractionCount(statsMap.getOrDefault(item.getId(), 0L));
        }
    }
}
