package com.af.tourism.service;

import com.af.tourism.pojo.dto.app.DiaryCommentCreateDTO;
import com.af.tourism.pojo.dto.app.DiaryCommentQueryDTO;
import com.af.tourism.pojo.vo.app.DiaryCommentCreateVO;
import com.af.tourism.pojo.vo.app.DiaryCommentVO;
import com.af.tourism.pojo.vo.common.PageResponse;

public interface DiaryCommentService {

    /**
     * 查询旅行日记评论列表
     * @param diaryId 旅行日记 id
     * @param queryDTO 旅行日记评论列表查询条件
     * @return 旅行日记评论列表
     */
    PageResponse<DiaryCommentVO> listComments(Long diaryId, DiaryCommentQueryDTO queryDTO);

    /**
     * 发表评论
     * @param diaryId 旅行日记 id
     * @param userId 评论作者
     * @param request 评论内容
     * @return 评论详情
     */
    DiaryCommentCreateVO createComment(Long diaryId, Long userId, DiaryCommentCreateDTO request);
}
