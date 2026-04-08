package com.af.tourism.controller.admin;

import com.af.tourism.common.ApiResponse;
import com.af.tourism.pojo.dto.admin.UserQueryDTO;
import com.af.tourism.pojo.vo.admin.UserForAdminVO;
import com.af.tourism.pojo.vo.common.PageResponse;
import com.af.tourism.securitylite.AuthContext;
import com.af.tourism.service.AuthService;
import com.af.tourism.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/admin")
@Validated
@RequiredArgsConstructor
public class AdminUserController {

    private final AuthService authService;
    private final UserService userService;

    /**
     * 获取用户列表
     * @param queryDTO 查询参数
     * @return 符合条件的用户列表
     */
    @GetMapping("/users")
    public ApiResponse<PageResponse<UserForAdminVO>> listUsers(@Valid UserQueryDTO queryDTO) {
        Long userId = AuthContext.requireCurrentUserId();
        authService.getCurrentAdminProfile(userId);
        return ApiResponse.ok(userService.listUsers(queryDTO));
    }
}
