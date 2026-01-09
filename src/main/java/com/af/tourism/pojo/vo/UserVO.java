package com.af.tourism.pojo.vo;

import lombok.Data;

/**
 * 用户对外展示 VO。
 */
@Data
public class UserVO {
    private Long id;
    private String username;
    private String email;
    private String avatarUrl;
    private Integer status;
}
