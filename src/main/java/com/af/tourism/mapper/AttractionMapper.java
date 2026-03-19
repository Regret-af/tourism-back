package com.af.tourism.mapper;

import com.af.tourism.pojo.dto.AttractionQueryDTO;
import com.af.tourism.pojo.entity.Attraction;
import com.af.tourism.pojo.vo.AttractionCardVO;
import com.af.tourism.pojo.vo.AttractionDetailVO;
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
     * @param attractionId
     * @return 景点信息
     */
    AttractionDetailVO selectAttractionDetail(@Param("attractionId") Long attractionId);
}
