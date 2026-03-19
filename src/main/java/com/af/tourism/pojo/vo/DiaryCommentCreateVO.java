package com.af.tourism.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 发表评论响应。
 */
@Data
public class DiaryCommentCreateVO {

    private Long id;
    private Long diaryId;
    private String content;
    private UserPublicVO author;
    private LocalDateTime createdAt;
}
