package com.af.tourism.pojo.vo.admin;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class UserStatusUpdateDTO {

    @NotNull(message = "status不能为空")
    @Min(value = 0, message = "status不能小于0")
    @Max(value = 1, message = "status不能大于1")
    private Integer status;
}
