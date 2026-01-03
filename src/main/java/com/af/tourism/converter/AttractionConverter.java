package com.af.tourism.converter;

import cn.hutool.core.util.StrUtil;
import com.af.tourism.pojo.entity.Attraction;
import com.af.tourism.pojo.vo.AttractionCardVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * MapStruct 景点实体与卡片 VO 转换。
 */
@Mapper(componentModel = "spring")
public interface AttractionConverter {

    @Mapping(target = "tags", expression = "java(parseTags(entity.getTags()))")
    AttractionCardVO toCardVO(Attraction entity);

    List<AttractionCardVO> toCardVOList(List<Attraction> list);

    /**
     * 解析逗号分隔标签。
     */
    default List<String> parseTags(String tags) {
        if (StrUtil.isBlank(tags)) {
            return Collections.emptyList();
        }
        List<String> list = new ArrayList<>();
        for (String tag : StrUtil.split(tags, ',')) {
            if (StrUtil.isNotBlank(tag)) {
                list.add(tag.trim());
            }
        }
        return list;
    }
}

