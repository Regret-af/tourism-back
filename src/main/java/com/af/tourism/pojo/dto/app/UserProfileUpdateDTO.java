package com.af.tourism.pojo.dto.app;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserProfileUpdateDTO {

    @NotBlank(message = "用户昵称不能为空")
    private String nickname;

    @NotBlank(message = "用户头像不能为空")
    private String avatarUrl;
}
