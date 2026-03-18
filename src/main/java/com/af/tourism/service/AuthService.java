package com.af.tourism.service;

import com.af.tourism.pojo.dto.LoginDTO;
import com.af.tourism.pojo.dto.RegisterDTO;
import com.af.tourism.pojo.vo.LoginVO;
import com.af.tourism.pojo.vo.RegisterVO;

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
}
