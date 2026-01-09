package com.af.tourism.pojo.dto;

import lombok.Data;

/**
 * 登录请求信息
 */
@Data
public class LoginDTO {

    String account;
    String password;
}
