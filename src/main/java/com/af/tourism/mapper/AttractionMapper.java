package com.af.tourism.mapper;

import com.af.tourism.pojo.dto.AttractionQueryDTO;
import com.af.tourism.pojo.entity.Attraction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AttractionMapper {

    List<Attraction> selectAttractions(@Param("queryDTO") AttractionQueryDTO queryDTO);
}
