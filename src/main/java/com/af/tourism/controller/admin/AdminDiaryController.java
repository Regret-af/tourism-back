package com.af.tourism.controller.admin;

import com.af.tourism.annotation.OperationLogRecord;
import com.af.tourism.common.ApiResponse;
import com.af.tourism.common.enums.OperationLogAction;
import com.af.tourism.common.enums.OperationLogModule;
import com.af.tourism.pojo.dto.admin.AdminDiaryQueryDTO;
import com.af.tourism.pojo.dto.admin.DiaryDeletedUpdateDTO;
import com.af.tourism.pojo.dto.admin.DiaryStatusUpdateDTO;
import com.af.tourism.pojo.vo.admin.DiaryDetailForAdminVO;
import com.af.tourism.pojo.vo.admin.DiaryForAdminVO;
import com.af.tourism.pojo.vo.common.PageResponse;
import com.af.tourism.service.admin.AdminDiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;

/**
 * 管理端日记接口
 */
@RestController
@Validated
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminDiaryController {

    private final AdminDiaryService adminDiaryService;

    /**
     * 获取管理端日记列表
     * @param queryDTO 查询参数
     * @return 日记分页列表
     */
    @GetMapping("/travel-diaries")
    public ApiResponse<PageResponse<DiaryForAdminVO>> listDiaries(@Valid AdminDiaryQueryDTO queryDTO) {
        return ApiResponse.ok(adminDiaryService.listDiaries(queryDTO));
    }

    /**
     * 获取管理端日记详情
     * @param id 日记 id
     * @return 日记详情
     */
    @GetMapping("/travel-diaries/{id}")
    public ApiResponse<DiaryDetailForAdminVO> getDiaryDetail(
            @PathVariable("id") @Min(value = 1, message = "id不能小于1") Long id) {
        return ApiResponse.ok(adminDiaryService.getDiaryDetail(id));
    }

    /**
     * 修改日记状态
     * @param id 日记 id
     * @param request 状态修改请求
     * @return 成功信息
     */
    @PatchMapping("/travel-diaries/{id}/status")
    @OperationLogRecord(
            module = OperationLogModule.DIARY,
            action = OperationLogAction.UPDATE_DIARY_STATUS,
            description = "修改日记状态",
            bizIdArgIndex = 0
    )
    public ApiResponse<Void> updateDiaryStatus(
            @PathVariable("id") @Min(value = 1, message = "id不能小于1") Long id,
            @Valid @RequestBody DiaryStatusUpdateDTO request) {
        adminDiaryService.updateDiaryStatus(id, request.getStatus());
        return ApiResponse.ok();
    }

    /**
     * 修改日记逻辑删除状态
     * @param id 日记 id
     * @param request 逻辑删除状态修改请求
     * @return 成功信息
     */
    @PatchMapping("/travel-diaries/{id}/deleted")
    @OperationLogRecord(
            module = OperationLogModule.DIARY,
            action = OperationLogAction.UPDATE_DIARY_DELETED,
            description = "修改日记逻辑删除状态",
            bizIdArgIndex = 0
    )
    public ApiResponse<Void> updateDiaryDeleted(
            @PathVariable("id") @Min(value = 1, message = "id不能小于1") Long id,
            @Valid @RequestBody DiaryDeletedUpdateDTO request) {
        adminDiaryService.updateDiaryDeleted(id, request.getIsDeleted());
        return ApiResponse.ok();
    }
}
