package com.af.tourism.service;

import com.af.tourism.pojo.dto.LoginDTO;
import com.af.tourism.pojo.vo.LoginVO;

public interface AuthService {

    LoginVO login(LoginDTO request);
}
