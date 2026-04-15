package com.af.tourism.service.impl.app;

import com.af.tourism.mapper.DiaryCategoryMapper;
import com.af.tourism.pojo.vo.common.OptionVO;
import com.af.tourism.service.app.DiaryCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiaryCategoryServiceImpl implements DiaryCategoryService {

    private final DiaryCategoryMapper diaryCategoryMapper;

    @Override
    public List<OptionVO<Long>> listCategoryOptions() {
        List<OptionVO<Long>> options = diaryCategoryMapper.selectDiaryCategoryOptions();
        if (options == null || options.isEmpty()) {
            log.debug("查询日记分类选项结果为空");
            return Collections.emptyList();
        }
        return options;
    }
}
