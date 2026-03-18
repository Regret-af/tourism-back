package com.af.tourism.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 登录请求信息
 */
@Data
public class LoginDTO {

    @NotBlank(message = "用户名不得为空")
    private String account;

    @NotBlank(message = "密码不得为空")
    private String password;
}
