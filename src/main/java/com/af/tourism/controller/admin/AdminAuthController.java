package com.af.tourism.controller.admin;

import com.af.tourism.common.ApiResponse;
import com.af.tourism.pojo.dto.common.LoginDTO;
import com.af.tourism.pojo.vo.common.LoginVO;
import com.af.tourism.pojo.vo.common.UserVO;
import com.af.tourism.securitylite.AuthContext;
import com.af.tourism.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/admin")
@Validated
@RequiredArgsConstructor
public class AdminAuthController {

    private final AuthService authService;

    /**
     * 管理员登录
     * @param loginDTO 登录参数
     * @return 登录信息响应
     */
    @PostMapping("/auth/login")
    public ApiResponse<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        return ApiResponse.ok(authService.adminLogin(loginDTO));
    }

    /**
     * 获取当前管理员信息
     * @return 当前管理员信息
     */
    @GetMapping("/auth/me")
    public ApiResponse<UserVO> me() {
        Long userId = AuthContext.requireCurrentUserId();
        return ApiResponse.ok(authService.getCurrentAdminProfile(userId));
    }
}
