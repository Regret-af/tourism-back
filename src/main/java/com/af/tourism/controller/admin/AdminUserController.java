package com.af.tourism.controller.admin;

import com.af.tourism.common.ApiResponse;
import com.af.tourism.pojo.dto.admin.UserQueryDTO;
import com.af.tourism.pojo.vo.admin.UserDetailForAdminVO;
import com.af.tourism.pojo.vo.admin.UserForAdminVO;
import com.af.tourism.pojo.vo.common.PageResponse;
import com.af.tourism.service.admin.AdminUserService;
import com.af.tourism.service.app.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;

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
     * 获取用户详情
     * @param userId 用户 id
     * @return 用户详情
     */
    @GetMapping("/users/{userId}")
    public ApiResponse<UserDetailForAdminVO> getUserDetail(
            @PathVariable("userId") @Min(value = 1, message = "userId不能小于1") Long userId) {
        return ApiResponse.ok(adminUserService.getUserDetail(userId));
    }
}
