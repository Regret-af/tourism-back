package com.af.tourism.mapper;

import com.af.tourism.pojo.dto.admin.AdminDiaryCommentQueryDTO;
import com.af.tourism.pojo.entity.DiaryComment;
import com.af.tourism.pojo.vo.admin.DiaryCommentForAdminVO;
import com.af.tourism.pojo.vo.app.DiaryCommentVO;
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
     * @return 评论列表
     */
    List<DiaryCommentVO> selectCommentList(@Param("diaryId") Long diaryId);

    /**
     * 查找指定旅行日记的评论总量
     * @param diaryId 日记 id
     * @return 评论数量
     */
    Integer countByDiaryId(@Param("diaryId") Long diaryId);

    /**
     * 查询管理端评论列表
     * @param queryDTO 查询条件
     * @return 评论分页列表
     */
    List<DiaryCommentForAdminVO> selectAdminCommentList(@Param("queryDTO") AdminDiaryCommentQueryDTO queryDTO);
}
