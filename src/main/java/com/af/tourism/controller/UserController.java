package com.af.tourism.controller;

import com.af.tourism.common.ApiResponse;
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
    public ApiResponse<UserPublicVO> updateProfile(@Valid @RequestBody UserProfileUpdateDTO profileUpdateDTO) {
        Long userId = AuthContext.requireCurrentUserId();
        return ApiResponse.ok(userService.updateUserProfile(userId, profileUpdateDTO));
    }
}
