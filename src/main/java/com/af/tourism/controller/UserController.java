package com.af.tourism.controller;

import com.af.tourism.common.ApiResponse;
import com.af.tourism.pojo.vo.UserVO;
import com.af.tourism.securitylite.AuthContext;
import com.af.tourism.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户相关接口。
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 获取当前用户信息
     * @return 当前用户信息
     */
    @GetMapping("/me")
    public ApiResponse<UserVO> me() {
        Long userId = AuthContext.requireCurrentUserId();
        return ApiResponse.ok(userService.getCurrentUserProfile(userId));
    }
}
