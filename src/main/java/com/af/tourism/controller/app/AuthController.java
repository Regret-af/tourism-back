package com.af.tourism.controller.app;

import com.af.tourism.annotation.OperationLogRecord;
import com.af.tourism.common.ApiResponse;
import com.af.tourism.common.enums.OperationLogAction;
import com.af.tourism.common.enums.OperationLogModule;
import com.af.tourism.pojo.dto.common.LoginDTO;
import com.af.tourism.pojo.dto.app.RegisterDTO;
import com.af.tourism.pojo.vo.common.LoginVO;
import com.af.tourism.pojo.vo.app.RegisterVO;
import com.af.tourism.service.app.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@Validated
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     * @param request 登录请求参数
     * @return 登录响应
     */
    @PostMapping("/login")
    @OperationLogRecord(module = OperationLogModule.USER, action = OperationLogAction.LOGIN, description = "用户登录", userIdField = "data.user.id")
    public ApiResponse<LoginVO> login(@Valid @RequestBody LoginDTO request) {
        return ApiResponse.ok(authService.login(request));
    }

    /**
     * 用户注册
     * @param request 注册请求参数
     * @return 注册响应
     */
    @PostMapping("/register")
    @OperationLogRecord(module = OperationLogModule.USER, action = OperationLogAction.REGISTER, description = "用户注册", userIdField = "data.id", bizIdField = "data.id")
    public ApiResponse<RegisterVO> register(@Valid @RequestBody RegisterDTO request) {
        return ApiResponse.ok(authService.register(request));
    }
}
