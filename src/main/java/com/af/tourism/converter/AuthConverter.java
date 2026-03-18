package com.af.tourism.converter;

import com.af.tourism.pojo.entity.User;
import com.af.tourism.pojo.vo.LoginUserVO;
import com.af.tourism.pojo.vo.LoginVO;
import com.af.tourism.pojo.vo.RegisterVO;
import com.af.tourism.pojo.vo.UserVO;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 认证相关对象转换器。
 */
@Component
public class AuthConverter {

    public LoginVO toLoginVO(User user, List<String> roles, String accessToken, String tokenType, Long expiresIn) {
        LoginVO loginVO = new LoginVO();
        loginVO.setAccessToken(accessToken);
        loginVO.setTokenType(tokenType);
        loginVO.setExpiresIn(expiresIn);
        loginVO.setUser(toLoginUserVO(user, roles));
        return loginVO;
    }

    public RegisterVO toRegisterVO(User user) {
        RegisterVO registerVO = new RegisterVO();
        registerVO.setId(user.getId());
        registerVO.setEmail(user.getEmail());
        registerVO.setUsername(user.getUsername());
        registerVO.setNickname(user.getNickname());
        return registerVO;
    }

    public UserVO toUserVO(User user, List<String> roles) {
        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setEmail(user.getEmail());
        userVO.setUsername(user.getUsername());
        userVO.setNickname(user.getNickname());
        userVO.setAvatarUrl(user.getAvatarUrl());
        userVO.setStatus(user.getStatus());
        userVO.setRoles(roles);
        userVO.setCreatedAt(user.getCreatedAt());
        return userVO;
    }

    private LoginUserVO toLoginUserVO(User user, List<String> roles) {
        LoginUserVO loginUserVO = new LoginUserVO();
        loginUserVO.setId(user.getId());
        loginUserVO.setEmail(user.getEmail());
        loginUserVO.setUsername(user.getUsername());
        loginUserVO.setNickname(user.getNickname());
        loginUserVO.setAvatarUrl(user.getAvatarUrl());
        loginUserVO.setRoles(roles);
        return loginUserVO;
    }
}
