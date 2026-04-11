package com.af.tourism.controller.admin;

import com.af.tourism.annotation.OperationLogRecord;
import com.af.tourism.common.ApiResponse;
import com.af.tourism.common.enums.OperationLogAction;
import com.af.tourism.common.enums.OperationLogModule;
import com.af.tourism.pojo.dto.admin.UserOptionQueryDTO;
import com.af.tourism.pojo.dto.admin.UserQueryDTO;
import com.af.tourism.pojo.vo.admin.UserDetailForAdminVO;
import com.af.tourism.pojo.vo.admin.UserForAdminVO;
import com.af.tourism.pojo.vo.admin.UserOptionForAdminVO;
import com.af.tourism.pojo.vo.admin.UserStatusUpdateDTO;
import com.af.tourism.pojo.vo.common.PageResponse;
import com.af.tourism.service.admin.AdminUserService;
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
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@Validated
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    /**
     * 获取用户列表
     * @param queryDTO 查询参数
     * @return 符合条件的用户列表
     */
    @GetMapping("/users")
    public ApiResponse<PageResponse<UserForAdminVO>> listUsers(@Valid UserQueryDTO queryDTO) {
        return ApiResponse.ok(adminUserService.listUsers(queryDTO));
    }

    /**
     * 获取用户下拉选项
     * @param queryDTO 查询参数
     * @return 用户下拉选项列表
     */
    @GetMapping("/users/options")
    public ApiResponse<List<UserOptionForAdminVO>> listUserOptions(@Valid UserOptionQueryDTO queryDTO) {
        return ApiResponse.ok(adminUserService.listUserOptions(queryDTO));
    }

    /**
     * 获取用户详情
     * @param userId 用户 id
     * @return 用户详情
     */
    @GetMapping("/users/{userId}")
    public ApiResponse<UserDetailForAdminVO> getUserDetail(
            @PathVariable("userId") @Min(value = 1, message = "userId不能小于1") Long userId) {
        return ApiResponse.ok(adminUserService.getUserDetail(userId));
    }

    /**
     * 修改用户状态
     * @param userId 用户 id
     * @param userStatusUpdateDTO 用户状态
     * @return 成功信息
     */
    @PatchMapping("/users/{userId}/status")
    @OperationLogRecord(
            module = OperationLogModule.USER,
            action = OperationLogAction.UPDATE_USER_STATUS,
            description = "修改用户状态",
            bizIdArgIndex = 0
    )
    public ApiResponse<Void> updateUserStatus(
            @PathVariable("userId") @Min(value = 1, message = "userId不能小于1") Long userId,
            @Valid @RequestBody UserStatusUpdateDTO userStatusUpdateDTO) {
        adminUserService.updateUserStatus(userId, userStatusUpdateDTO.getStatus());
        return ApiResponse.ok();
    }
}
