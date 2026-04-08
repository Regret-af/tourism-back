package com.af.tourism.pojo.dto.app;

import com.af.tourism.common.enums.NotificationType;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 通知列表查询参数。
 */
@Data
public class NotificationQueryDTO {

    @Min(value = 1, message = "pageNum不能小于1")
    private Integer pageNum = 1;

    @Min(value = 1, message = "pageSize不能小于1")
    @Max(value = 50, message = "pageSize不能大于50")
    private Integer pageSize = 10;

    private Boolean isRead;

    private NotificationType type;
}
