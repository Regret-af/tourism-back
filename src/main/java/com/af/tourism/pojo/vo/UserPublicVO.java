package com.af.tourism.pojo.vo;

import lombok.Data;

/**
 * 对外公开的用户信息。
 */
@Data
public class UserPublicVO {
    private Long id;
    private String username;
    private String avatarUrl;
    private String bio;
}

