package com.af.tourism.pojo.dto.app;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UserProfileUpdateDTO {

    @NotBlank(message = "用户昵称不能为空")
    private String nickname;

    @NotBlank(message = "用户头像不能为空")
    private String avatarUrl;

    @Size(max = 255, message = "bio长度不能超过255")
    private String bio;
}
