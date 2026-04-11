package com.af.tourism.pojo.dto.admin;

import com.af.tourism.common.enums.OperationLogAction;
import com.af.tourism.common.enums.OperationLogModule;
import com.af.tourism.common.enums.OperationLogSource;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

/**
 * 管理端操作日志列表查询参数
 */
@Data
public class OperationLogQueryDTO {

    @Min(value = 1, message = "pageNum不能小于1")
    private Integer pageNum = 1;

    @Min(value = 1, message = "pageSize不能小于1")
    @Max(value = 50, message = "pageSize不能大于50")
    private Integer pageSize = 10;

    @Min(value = 1, message = "userId不能小于1")
    private Long userId;

    private OperationLogModule module;

    private OperationLogAction action;

    private OperationLogSource source;

    private String requestIp;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdStart;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdEnd;
}
