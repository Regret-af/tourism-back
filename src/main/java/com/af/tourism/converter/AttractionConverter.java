package com.af.tourism.converter;

import com.af.tourism.pojo.dto.admin.AdminAttractionCreateDTO;
import com.af.tourism.pojo.dto.admin.AdminAttractionUpdateDTO;
import com.af.tourism.pojo.dto.admin.AttractionCategoryCreateDTO;
import com.af.tourism.pojo.dto.admin.AttractionCategoryUpdateDTO;
import com.af.tourism.pojo.entity.Attraction;
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

    AttractionCategory toAttractionCategory(AttractionCategoryCreateDTO attractionCategoryCreateDTO);

    Attraction toAttraction(AdminAttractionCreateDTO adminAttractionCreateDTO);

    Attraction toAttraction(AdminAttractionUpdateDTO adminAttractionUpdateDTO);
}
