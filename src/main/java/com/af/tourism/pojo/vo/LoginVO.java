package com.af.tourism.pojo.vo;

import lombok.Data;

@Data
public class LoginVO {

    private Integer id;
    private String username;
    private String email;
    private String avatarUrl;
    private Integer status;
    private String accessToken;
    private String tokenType;
    private Long expiresIn;
}
