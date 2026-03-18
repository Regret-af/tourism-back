package com.af.tourism.mapper;

import com.af.tourism.pojo.dto.DiaryQueryDTO;
import com.af.tourism.pojo.entity.DiaryWithUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 日记列表查询 Mapper（XML）。
 */
@Mapper
public interface DiaryMapper {

    List<DiaryWithUser> selectDiaryList(@Param("queryDTO") DiaryQueryDTO queryDTO);
}
