package com.af.tourism.converter;

import com.af.tourism.pojo.entity.User;
import com.af.tourism.pojo.vo.LoginUserVO;
import com.af.tourism.pojo.vo.LoginVO;
import com.af.tourism.pojo.vo.RegisterVO;
import com.af.tourism.pojo.vo.UserVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * 认证相关对象转换器。
 */
@Mapper(componentModel = "spring")
public interface AuthConverter {

    default LoginVO toLoginVO(User user, List<String> roles, String accessToken, String tokenType, Long expiresIn) {
        LoginVO loginVO = new LoginVO();
        loginVO.setAccessToken(accessToken);
        loginVO.setTokenType(tokenType);
        loginVO.setExpiresIn(expiresIn);
        loginVO.setUser(toLoginUserVO(user, roles));
        return loginVO;
    }

    RegisterVO toRegisterVO(User user);

    @Mapping(target = "roles", source = "roles")
    UserVO toUserVO(User user, List<String> roles);

    @Mapping(target = "roles", source = "roles")
    LoginUserVO toLoginUserVO(User user, List<String> roles);
}
