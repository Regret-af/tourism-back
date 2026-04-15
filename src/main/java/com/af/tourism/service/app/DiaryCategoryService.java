package com.af.tourism.service.app;

import com.af.tourism.pojo.vo.common.OptionVO;

import java.util.List;

public interface DiaryCategoryService {

    List<OptionVO<Long>> listCategoryOptions();
}
