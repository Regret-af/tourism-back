package com.af.tourism.service.admin;

import com.af.tourism.pojo.dto.admin.OperationLogQueryDTO;
import com.af.tourism.pojo.vo.admin.OperationLogDetailForAdminVO;
import com.af.tourism.pojo.vo.admin.OperationLogForAdminVO;
import com.af.tourism.pojo.vo.common.PageResponse;

import javax.validation.Valid;

/**
 * 管理端操作日志服务
 */
public interface AdminOperationLogService {

    /**
     * 操作日志列表
     * @param queryDTO 查询参数
     * @return 操作日志分页列表
     */
    PageResponse<OperationLogForAdminVO> listOperationLogs(@Valid OperationLogQueryDTO queryDTO);

    /**
     * 操作日志详情
     * @param id 日志 id
     * @return 操作日志详情
     */
    OperationLogDetailForAdminVO getOperationLogDetail(Long id);
}
