package com.af.tourism.mapper;

import com.af.tourism.pojo.entity.Attraction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AttractionMapper {

    List<Attraction> selectAttractions(@Param("q") String q,
                                       @Param("location") String location,
                                       @Param("priceLevel") Integer priceLevel,
                                       @Param("tags") List<String> tags,
                                       @Param("sortColumn") String sortColumn,
                                       @Param("sortOrder") String sortOrder);
}
