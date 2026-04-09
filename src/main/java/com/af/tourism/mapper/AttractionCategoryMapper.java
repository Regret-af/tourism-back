package com.af.tourism.mapper;

import com.af.tourism.pojo.dto.admin.AttractionCategoryQueryDTO;
import com.af.tourism.pojo.entity.AttractionCategory;
import com.af.tourism.pojo.vo.admin.AttractionCategoryForAdminVO;
import com.af.tourism.pojo.vo.admin.AttractionCategoryStatsForAdminVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 景点分类 Mapper
 */
@Mapper
public interface AttractionCategoryMapper extends BaseMapper<AttractionCategory> {

    /**
     * 查找状态为启用的景点分类
     * @return 按 sort_order、id 升序排列
     */
    List<AttractionCategory> selectEnabledCategories();

    /**
     * 管理端景点分类列表
     * @param queryDTO 查询参数
     * @return 分类列表
     */
    List<AttractionCategoryForAdminVO> selectAdminCategoryList(@Param("queryDTO") AttractionCategoryQueryDTO queryDTO);

    /**
     * 批量查询分类下景点数量
     * @param categoryIds 分类 id 列表
     * @return 分类统计列表
     */
    List<AttractionCategoryStatsForAdminVO> selectAttractionStatsByCategoryIds(@Param("categoryIds") List<Long> categoryIds);
}
