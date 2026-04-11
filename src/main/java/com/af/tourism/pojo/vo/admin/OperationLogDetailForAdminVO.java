package com.af.tourism.pojo.vo.admin;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 管理端操作日志详情
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OperationLogDetailForAdminVO extends OperationLogForAdminVO {

    private String userAgent;
}
