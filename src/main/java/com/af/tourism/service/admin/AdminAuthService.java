package com.af.tourism.service.admin;

import com.af.tourism.pojo.dto.common.LoginDTO;
import com.af.tourism.pojo.vo.common.LoginVO;
import com.af.tourism.pojo.vo.common.UserVO;

public interface AdminAuthService {

    /**
     * 管理员登录
     * @param request 登录参数
     * @return 登录信息响应
     */
    LoginVO adminLogin(LoginDTO request);

    /**
     * 获取当前管理员信息
     * @param userId 当前登录管理员 id
     * @return 当前管理员信息
     */
    UserVO getCurrentAdminProfile(Long userId);

    /**
     * 管理员登出
     * @param authorizationHeader Authorization 请求头
     */
    void logout(String authorizationHeader);
}
