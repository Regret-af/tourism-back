package com.af.tourism.service;

import com.af.tourism.pojo.dto.LoginDTO;
import com.af.tourism.pojo.dto.RegisterDTO;
import com.af.tourism.pojo.vo.LoginVO;
import com.af.tourism.pojo.vo.UserVO;

public interface AuthService {

    LoginVO login(LoginDTO request);

    UserVO register(RegisterDTO request);
}
