package com.af.tourism.pojo.vo;

import lombok.Data;

import java.util.List;

/**
 * 登录响应中的用户信息。
 */
@Data
public class LoginUserVO {

    private Long id;
    private String email;
    private String username;
    private String nickname;
    private String avatarUrl;
    private List<String> roles;
}
