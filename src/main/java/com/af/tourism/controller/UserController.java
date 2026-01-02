package com.af.tourism.controller;

import com.af.tourism.common.ApiResponse;
import com.af.tourism.common.ErrorCode;
import com.af.tourism.exception.BusinessException;
import com.af.tourism.exception.UnauthorizedException;
import com.af.tourism.pojo.entity.User;
import com.af.tourism.pojo.vo.UserVO;
import com.af.tourism.securitylite.AuthContext;
import com.af.tourism.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户相关接口示例。
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ApiResponse<UserVO> me() {
        Long userId = AuthContext.getUserId();
        if (userId == null) {
            throw new UnauthorizedException("未登录");
        }
        User user = userService.findById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return ApiResponse.ok(vo);
    }
}
