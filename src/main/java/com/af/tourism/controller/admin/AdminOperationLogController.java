package com.af.tourism.controller.admin;

import com.af.tourism.common.ApiResponse;
import com.af.tourism.pojo.dto.admin.OperationLogQueryDTO;
import com.af.tourism.pojo.vo.admin.OperationLogDetailForAdminVO;
import com.af.tourism.pojo.vo.admin.OperationLogForAdminVO;
import com.af.tourism.pojo.vo.common.PageResponse;
import com.af.tourism.service.admin.AdminOperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;

/**
 * 管理端操作日志接口
 */
@RestController
@Validated
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminOperationLogController {

    private final AdminOperationLogService adminOperationLogService;

    /**
     * 操作日志列表
     * @param queryDTO 查询参数
     * @return 操作日志分页列表
     */
    @GetMapping("/operation-logs")
    public ApiResponse<PageResponse<OperationLogForAdminVO>> listOperationLogs(@Valid OperationLogQueryDTO queryDTO) {
        return ApiResponse.ok(adminOperationLogService.listOperationLogs(queryDTO));
    }

    /**
     * 操作日志详情
     * @param id 日志 id
     * @return 操作日志详情
     */
    @GetMapping("/operation-logs/{id}")
    public ApiResponse<OperationLogDetailForAdminVO> getOperationLogDetail(
            @PathVariable("id") @Min(value = 1, message = "id不能小于1") Long id) {
        return ApiResponse.ok(adminOperationLogService.getOperationLogDetail(id));
    }
}
