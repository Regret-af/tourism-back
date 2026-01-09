package com.af.tourism.pojo.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class RegisterDTO {

    @NotBlank(message = "用户名不得为空")
    private String username;

    @NotBlank(message = "邮箱不得为空")
    @Email(message = "输入邮箱无效")
    private String email;

    @NotBlank(message = "密码不得为空")
    private String password;

    private String confirmPassword;
}
