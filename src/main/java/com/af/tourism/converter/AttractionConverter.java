package com.af.tourism.converter;

import com.af.tourism.pojo.entity.AttractionCategory;
import com.af.tourism.pojo.vo.app.AttractionCategoryVO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * 景点相关对象转换器
 */
@Mapper(componentModel = "spring")
public interface AttractionConverter {

    AttractionCategoryVO toAttractionCategoryVO(AttractionCategory attractionCategory);

    List<AttractionCategoryVO> toAttractionCategoryVOList(List<AttractionCategory> attractionCategoryList);
}
