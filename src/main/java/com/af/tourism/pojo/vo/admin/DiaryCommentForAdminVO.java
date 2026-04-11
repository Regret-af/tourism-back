package com.af.tourism.pojo.vo.admin;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理端评论列表项
 */
@Data
public class DiaryCommentForAdminVO {

    private Long id;

    private Long diaryId;

    private String diaryTitle;

    private Long userId;

    private String userNickname;

    private Long parentId;

    private Long replyToUserId;

    private String content;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
