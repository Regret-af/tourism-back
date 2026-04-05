package com.af.tourism.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 当前用户信息响应。
 */
@Data
public class UserVO {

    private Long id;
    private String email;
    private String username;
    private String nickname;
    private String avatarUrl;
    private String bio;
    private Integer status;
    private List<String> roles;
    private LocalDateTime createdAt;
}
