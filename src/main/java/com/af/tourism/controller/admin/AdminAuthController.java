package com.af.tourism.controller.admin;

import com.af.tourism.annotation.OperationLogRecord;
import com.af.tourism.common.ApiResponse;
import com.af.tourism.common.enums.OperationLogAction;
import com.af.tourism.common.enums.OperationLogModule;
import com.af.tourism.pojo.dto.common.LoginDTO;
import com.af.tourism.pojo.vo.common.LoginVO;
import com.af.tourism.pojo.vo.common.UserVO;
import com.af.tourism.security.model.SecurityUser;
import com.af.tourism.security.util.SecurityUtils;
import com.af.tourism.service.admin.AdminAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/admin")
@Validated
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    /**
     * 管理员登录
     * @param loginDTO 登录参数
     * @return 登录信息响应
     */
    @PostMapping("/auth/login")
    @OperationLogRecord(
            module = OperationLogModule.USER,
            action = OperationLogAction.ADMIN_LOGIN,
            description = "管理员登录",
            userIdField = "data.user.id"
    )
    public ApiResponse<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        return ApiResponse.ok(adminAuthService.adminLogin(loginDTO));
    }

    /**
     * 获取当前管理员信息
     * @return 当前管理员信息
     */
    @GetMapping("/auth/me")
    public ApiResponse<UserVO> me(@AuthenticationPrincipal SecurityUser currentUser) {
        return ApiResponse.ok(adminAuthService.getCurrentAdminProfile(currentUser.getUserId()));
    }

    /**
     * 管理员退出登录
     * @return 退出登录信息
     */
    @PostMapping("/auth/logout")
    @OperationLogRecord(
            module = OperationLogModule.USER,
            action = OperationLogAction.ADMIN_LOGOUT,
            description = "管理员退出登录"
    )
    public ApiResponse<Void> logout() {
        // 获取当前登录管理员信息，确保真实登录
        Long userId = SecurityUtils.requireCurrentUserId();
        // 暂无业务处理，直接返回
        return ApiResponse.ok();
    }
}
