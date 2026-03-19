package com.af.tourism.mapper;

import com.af.tourism.pojo.entity.DiaryComment;
import com.af.tourism.pojo.vo.DiaryCommentVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 评论 Mapper。
 */
@Mapper
public interface DiaryCommentMapper extends BaseMapper<DiaryComment> {

    /**
     * 查找指定旅行日记的所有启用状态的一级评论列表
     * @param diaryId 旅行日记 id
     * @return
     */
    List<DiaryCommentVO> selectCommentList(@Param("diaryId") Long diaryId);

    /**
     * 查找指定旅行日记的评论总量
     * @param diaryId
     * @return
     */
    Integer countByDiaryId(@Param("diaryId") Long diaryId);
}
