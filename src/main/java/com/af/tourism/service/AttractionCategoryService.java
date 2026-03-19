package com.af.tourism.service;

import com.af.tourism.pojo.vo.AttractionCategoryVO;

import java.util.List;

public interface AttractionCategoryService {

    /**
     * 景点分类列表
     * @return 景点分类列表项
     */
    List<AttractionCategoryVO> listCategories();
}
