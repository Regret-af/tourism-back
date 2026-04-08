package com.af.tourism.pojo.vo.app;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论列表项。
 */
@Data
public class DiaryCommentVO {

    private Long id;
    private String content;
    private UserPublicVO author;
    private LocalDateTime createdAt;
}
