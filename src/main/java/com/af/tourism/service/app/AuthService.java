package com.af.tourism.service.app;

import com.af.tourism.pojo.dto.app.RegisterDTO;
import com.af.tourism.pojo.dto.common.LoginDTO;
import com.af.tourism.pojo.vo.app.RegisterVO;
import com.af.tourism.pojo.vo.common.LoginVO;

public interface AuthService {

    /**
     * 用户登录
     * @param request 登录请求参数
     * @return 登录响应
     */
    LoginVO login(LoginDTO request);

    /**
     * 用户注册
     * @param request 注册请求参数
     * @return 注册响应
     */
    RegisterVO register(RegisterDTO request);

    /**
     * 用户登出
     * @param authorizationHeader Authorization 请求头
     */
    void logout(String authorizationHeader);
}
