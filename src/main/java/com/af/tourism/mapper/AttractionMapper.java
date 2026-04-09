package com.af.tourism.mapper;

import com.af.tourism.pojo.dto.admin.AdminAttractionQueryDTO;
import com.af.tourism.pojo.dto.app.AttractionQueryDTO;
import com.af.tourism.pojo.entity.Attraction;
import com.af.tourism.pojo.vo.admin.AttractionDetailForAdminVO;
import com.af.tourism.pojo.vo.admin.AttractionForAdminVO;
import com.af.tourism.pojo.vo.app.AttractionCardVO;
import com.af.tourism.pojo.vo.app.AttractionDetailVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AttractionMapper extends BaseMapper<Attraction> {

    /**
     * 查询符合条件的启用景点
     * @param queryDTO 列表、搜索、分类筛选参数
     * @return 景点列表
     */
    List<AttractionCardVO> selectAttractions(@Param("queryDTO") AttractionQueryDTO queryDTO);

    /**
     * 根据 id 查询启用景点的信息
     * @param attractionId 景点 id
     * @return 景点信息
     */
    AttractionDetailVO selectAttractionDetail(@Param("attractionId") Long attractionId);

    /**
     * 管理端景点列表
     * @param queryDTO 查询参数
     * @return 景点列表
     */
    List<AttractionForAdminVO> selectAdminAttractions(@Param("queryDTO") AdminAttractionQueryDTO queryDTO);

    /**
     * 管理端景点详情
     * @param attractionId 景点 id
     * @return 景点详情信息
     */
    AttractionDetailForAdminVO selectAdminAttractionDetail(@Param("attractionId") Long attractionId);
}
