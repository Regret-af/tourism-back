package com.af.tourism.service;

import com.af.tourism.pojo.dto.common.LoginDTO;
import com.af.tourism.pojo.dto.app.RegisterDTO;
import com.af.tourism.pojo.vo.common.LoginVO;
import com.af.tourism.pojo.vo.app.RegisterVO;

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
     * 管理员登录
     * @param request 登录参数
     * @return 登录信息响应
     */
    LoginVO adminLogin(LoginDTO request);
}
