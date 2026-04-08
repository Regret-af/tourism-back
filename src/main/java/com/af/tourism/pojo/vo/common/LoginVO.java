package com.af.tourism.pojo.vo.common;

import lombok.Data;

/**
 * 登录响应。
 */
@Data
public class LoginVO {

    private String accessToken;
    private String tokenType;
    private Long expiresIn;
    private LoginUserVO user;
}
