package com.af.tourism.service.impl;

import com.af.tourism.converter.AttractionConverter;
import com.af.tourism.converter.AttractionConverterImpl;
import com.af.tourism.mapper.AttractionCategoryMapper;
import com.af.tourism.pojo.entity.AttractionCategory;
import com.af.tourism.pojo.vo.AttractionCategoryVO;
import com.af.tourism.service.AttractionCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttractionCategoryServiceImpl implements AttractionCategoryService {

    private final AttractionCategoryMapper attractionCategoryMapper;
    private final AttractionConverter attractionConverter;

    /**
     * 景点分类列表
     * @return 景点分类列表项
     */
    @Override
    public List<AttractionCategoryVO> listCategories() {
        // 1.查询状态为启用的景点分类列表
        List<AttractionCategory> categories = attractionCategoryMapper.selectEnabledCategories();

        // 2.检查是否为空
        if (categories == null || categories.isEmpty()) {
            return Collections.emptyList();
        }

        // 3.使用转换器进行字段映射
        return attractionConverter.toAttractionCategoryVOList(categories);
    }
}
