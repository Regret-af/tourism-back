package com.af.tourism.controller;

import com.af.tourism.annotation.OperationLogRecord;
import com.af.tourism.common.ApiResponse;
import com.af.tourism.common.enums.OperationLogAction;
import com.af.tourism.common.enums.OperationLogModule;
import com.af.tourism.pojo.dto.UserPasswordUpdateDTO;
import com.af.tourism.pojo.dto.UserProfileUpdateDTO;
import com.af.tourism.pojo.vo.UserPublicVO;
import com.af.tourism.pojo.vo.UserVO;
import com.af.tourism.securitylite.AuthContext;
import com.af.tourism.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 用户相关接口。
 */
@RestController
@Validated
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取当前用户信息
     * @return 当前用户信息
     */
    @GetMapping("/me")
    public ApiResponse<UserVO> me() {
        Long userId = AuthContext.requireCurrentUserId();
        return ApiResponse.ok(userService.getCurrentUserProfile(userId));
    }

    /**
     * 更新当前用户资料
     * @param profileUpdateDTO 用户信息
     * @return 更新后的用户信息
     */
    @PutMapping("/me/profile")
    @OperationLogRecord(module = OperationLogModule.USER, action = OperationLogAction.UPDATE_PROFILE, description = "用户修改个人信息", userIdField = "data.id")
    public ApiResponse<UserPublicVO> updateProfile(@Valid @RequestBody UserProfileUpdateDTO profileUpdateDTO) {
        Long userId = AuthContext.requireCurrentUserId();
        return ApiResponse.ok(userService.updateUserProfile(userId, profileUpdateDTO));
    }

    /**
     * 修改当前用户密码
     * @param passwordUpdateDTO 新旧密码
     * @return 操作结果
     */
    @PutMapping("/me/password")
    @OperationLogRecord(module = OperationLogModule.USER, action = OperationLogAction.UPDATE_PASSWORD, description = "用户修改密码")
    public ApiResponse<Boolean> updatePassword(@Valid @RequestBody UserPasswordUpdateDTO passwordUpdateDTO) {
        Long userId = AuthContext.requireCurrentUserId();
        return ApiResponse.ok(userService.updatePassword(userId, passwordUpdateDTO));
    }
}
