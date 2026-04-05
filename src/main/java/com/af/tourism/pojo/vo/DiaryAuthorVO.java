package com.af.tourism.pojo.vo;

import lombok.Data;

/**
 * 笔记作者信息
 */
@Data
public class DiaryAuthorVO {

    private Long id;
    private String nickname;
    private String avatarUrl;
    private String bio;
}
