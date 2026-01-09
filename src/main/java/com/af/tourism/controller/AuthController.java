package com.af.tourism.controller;

import com.af.tourism.common.ApiResponse;
import com.af.tourism.pojo.dto.LoginDTO;
import com.af.tourism.pojo.vo.LoginVO;
import com.af.tourism.service.AuthService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@Validated
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginVO> login(@Valid @RequestBody LoginDTO request) {
        return ApiResponse.ok(authService.login(request));
    }
}
