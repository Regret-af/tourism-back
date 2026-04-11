package com.af.tourism.service.admin;

import com.af.tourism.common.enums.DiaryCommentStatus;
import com.af.tourism.pojo.dto.admin.AdminDiaryCommentQueryDTO;
import com.af.tourism.pojo.vo.admin.DiaryCommentForAdminVO;
import com.af.tourism.pojo.vo.common.PageResponse;

import javax.validation.Valid;

/**
 * 管理端评论服务
 */
public interface AdminDiaryCommentService {

    /**
     * 获取评论列表
     * @param queryDTO 查询参数
     * @return 评论分页列表
     */
    PageResponse<DiaryCommentForAdminVO> listComments(@Valid AdminDiaryCommentQueryDTO queryDTO);

    /**
     * 修改评论状态
     * @param id 评论 id
     * @param status 目标状态
     */
    void updateCommentStatus(Long id, DiaryCommentStatus status);
}
