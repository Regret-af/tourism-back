package com.af.tourism.pojo.dto.admin;

import com.af.tourism.common.enums.RoleCode;
import com.af.tourism.common.enums.UserStatus;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

/**
 * 用户列表查询参数
 */
@Data
public class UserQueryDTO {

    @Min(value = 1, message = "pageNum不能小于1")
    private Integer pageNum = 1;

    @Min(value = 1, message = "pageSize不能小于1")
    @Max(value = 50, message = "pageSize不能大于50")
    private Integer pageSize = 10;

    private String keyword;

    private UserStatus status;

    private RoleCode roleCode;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdStart;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdEnd;
}
