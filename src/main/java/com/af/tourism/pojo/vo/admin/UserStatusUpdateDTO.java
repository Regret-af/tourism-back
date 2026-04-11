package com.af.tourism.pojo.vo.admin;

import com.af.tourism.common.enums.UserStatus;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserStatusUpdateDTO {

    @NotNull(message = "status不能为空")
    private UserStatus status;
}
