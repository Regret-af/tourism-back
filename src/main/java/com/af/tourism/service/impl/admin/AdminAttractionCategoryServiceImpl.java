package com.af.tourism.service.impl.admin;

import com.af.tourism.common.ErrorCode;
import com.af.tourism.converter.AttractionConverter;
import com.af.tourism.exception.BusinessException;
import com.af.tourism.mapper.AttractionMapper;
import com.af.tourism.mapper.AttractionCategoryMapper;
import com.af.tourism.pojo.dto.admin.AttractionCategoryCreateDTO;
import com.af.tourism.pojo.dto.admin.AttractionCategoryQueryDTO;
import com.af.tourism.pojo.dto.admin.AttractionCategorySortOrderUpdateDTO;
import com.af.tourism.pojo.dto.admin.AttractionCategoryStatusUpdateDTO;
import com.af.tourism.pojo.dto.admin.AttractionCategoryUpdateDTO;
import com.af.tourism.pojo.entity.Attraction;
import com.af.tourism.pojo.entity.AttractionCategory;
import com.af.tourism.pojo.vo.admin.AttractionCategoryForAdminVO;
import com.af.tourism.pojo.vo.admin.AttractionCategoryStatsForAdminVO;
import com.af.tourism.pojo.vo.common.PageResponse;
import com.af.tourism.service.admin.AdminAttractionCategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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

    private final AttractionMapper attractionMapper;

    private final AttractionCategoryMapper attractionCategoryMapper;

    private final AttractionConverter attractionConverter;

    /**
     * 获取管理端景点分类列表
     * @param queryDTO 查询参数
     * @return 分类分页列表
     */
    @Override
    public PageResponse<AttractionCategoryForAdminVO> listCategories(AttractionCategoryQueryDTO queryDTO) {
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());

        List<AttractionCategoryForAdminVO> list = attractionCategoryMapper.selectAdminCategoryList(queryDTO);
        PageInfo<AttractionCategoryForAdminVO> pageInfo = new PageInfo<>(list);

        fillAttractionCount(list);

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
        AttractionCategoryForAdminVO detail = attractionCategoryMapper.selectAdminCategoryDetailById(id);
        if (detail == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "景点分类不存在");
        }

        fillAttractionCount(Collections.singletonList(detail));
        return detail;
    }

    /**
     * 新增景点分类
     * @param request 新增请求
     * @return 新增后的分类详情
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AttractionCategoryForAdminVO createCategory(AttractionCategoryCreateDTO request) {
        // 1.校验参数
        if (!StringUtils.hasText(request.getName()) || !StringUtils.hasText(request.getCode())) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "分类名称或编码不能为空");
        }

        // 2.转为实体，直接插入
        AttractionCategory entity = attractionConverter.toAttractionCategory(request);

        try {
            attractionCategoryMapper.insert(entity);
        } catch (DuplicateKeyException ex) {
            throw new BusinessException(ErrorCode.CONFLICT, "分类编码已存在");
        }

        return getCategoryDetail(entity.getId());
    }

    /**
     * 编辑景点分类
     * @param id 分类 id
     * @param request 编辑请求
     * @return 编辑后的分类详情
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AttractionCategoryForAdminVO updateCategory(Long id, AttractionCategoryUpdateDTO request) {
        // 1.获取景点分类实体
        AttractionCategory entity = attractionCategoryMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "景点分类不存在");
        }

        // 2.校验参数合法性
        if (!StringUtils.hasText(request.getName()) || !StringUtils.hasText(request.getCode())) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "分类名称或编码不能为空");
        }

        // 3.填充参数并更新分类
        entity.setName(request.getName());
        entity.setCode(request.getCode());

        try {
            attractionCategoryMapper.updateById(entity);
        } catch (DuplicateKeyException ex) {
            throw new BusinessException(ErrorCode.CONFLICT, "分类编码已存在");
        }

        return getCategoryDetail(id);
    }

    /**
     * 修改景点分类状态
     * @param id 分类 id
     * @param request 状态修改请求
     * @return 修改后的分类详情
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AttractionCategoryForAdminVO updateCategoryStatus(Long id, AttractionCategoryStatusUpdateDTO request) {
        // 1.获取景点分类实体
        AttractionCategory entity = attractionCategoryMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "景点分类不存在");
        }

        // 2.校验参数合法性
        Integer status = request.getStatus();
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "分类状态不合法");
        }

        // 3.如果该分类下存在上架景点，不允许停用
        if (status == 0) {
            Long enabledAttractionCount = attractionMapper.selectCount(
                    new LambdaQueryWrapper<Attraction>()
                            .eq(Attraction::getCategoryId, id)
                            .eq(Attraction::getStatus, 1)
            );
            if (enabledAttractionCount != null && enabledAttractionCount > 0) {
                throw new BusinessException(ErrorCode.BUSINESS_ERROR, "该分类下仍存在上架景点，不能直接停用");
            }
        }

        // 4.进行状态的更新
        entity.setStatus(status);
        attractionCategoryMapper.updateById(entity);
        return getCategoryDetail(id);
    }

    /**
     * 修改景点分类排序
     * @param id 分类 id
     * @param request 排序修改请求
     * @return 修改后的分类详情
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AttractionCategoryForAdminVO updateCategorySortOrder(Long id, AttractionCategorySortOrderUpdateDTO request) {
        // 1.获取景点分类实体
        AttractionCategory entity = attractionCategoryMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "景点分类不存在");
        }

        // 2.更新景点分类排序
        entity.setSortOrder(request.getSortOrder());
        attractionCategoryMapper.updateById(entity);
        return getCategoryDetail(id);
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
