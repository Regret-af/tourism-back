package com.af.tourism.pojo.dto.app;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserPasswordUpdateDTO {

    @NotBlank(message = "旧密码不能为空")
    private String currentPassword;

    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}
