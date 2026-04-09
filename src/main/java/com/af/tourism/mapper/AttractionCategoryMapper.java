package com.af.tourism.mapper;

import com.af.tourism.pojo.dto.admin.AttractionCategoryQueryDTO;
import com.af.tourism.pojo.entity.AttractionCategory;
import com.af.tourism.pojo.vo.admin.AttractionCategoryForAdminVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 景点分类 Mapper。
 */
@Mapper
public interface AttractionCategoryMapper extends BaseMapper<AttractionCategory> {

    /**
     * 查找状态为启用的景点类别
     * @return 以 sort_order 字段升序，以 id 升序
     */
    List<AttractionCategory> selectEnabledCategories();

    /**
     * 管理端景点分类列表
     * @param queryDTO 查询参数
     * @return 分类列表
     */
    List<AttractionCategoryForAdminVO> selectAdminCategoryList(@Param("queryDTO") AttractionCategoryQueryDTO queryDTO);
}
