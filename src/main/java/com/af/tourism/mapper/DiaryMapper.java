package com.af.tourism.mapper;

import com.af.tourism.pojo.dto.DiaryQueryDTO;
import com.af.tourism.pojo.entity.TravelDiary;
import com.af.tourism.pojo.vo.DiaryCardVO;
import com.af.tourism.pojo.vo.DiaryDetailVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 旅行日记 Mapper。
 */
@Mapper
public interface DiaryMapper extends BaseMapper<TravelDiary> {

    /**
     * 查询符合条件的旅行日记
     * @param queryDTO 查询条件
     * @return 旅行日记列表
     */
    List<DiaryCardVO> selectDiaryList(@Param("queryDTO") DiaryQueryDTO queryDTO);

    /**
     * 查询旅行日记详情
     * @param diaryId 日记 id
     * @return 旅行日记详情
     */
    DiaryDetailVO selectDiaryDetail(@Param("diaryId") Long diaryId);
}
