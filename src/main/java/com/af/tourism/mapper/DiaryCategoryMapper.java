package com.af.tourism.mapper;

import com.af.tourism.pojo.entity.DiaryCategory;
import com.af.tourism.pojo.vo.admin.DiaryContentTypeOptionVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 日记分类 Mapper
 */
@Mapper
public interface DiaryCategoryMapper extends BaseMapper<DiaryCategory> {

    /**
     * 查询管理端日记类型字典
     * @return 日记类型选项列表
     */
    List<DiaryContentTypeOptionVO> selectAdminDiaryContentTypes();
}
