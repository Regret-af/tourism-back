package com.af.tourism.converter;

import com.af.tourism.pojo.entity.User;
import com.af.tourism.pojo.vo.LoginVO;
import com.af.tourism.pojo.vo.UserVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct 用户实体与返回信息 VO 转换。
 */
@Mapper(componentModel = "spring")
public interface AuthConverter {

    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "avatarUrl", source = "user.avatarUrl")
    @Mapping(target = "status", source = "user.status")
    @Mapping(target = "accessToken", expression = "java(accessToken)")
    @Mapping(target = "tokenType", expression = "java(tokenType)")
    @Mapping(target = "expiresIn", expression = "java(expiresIn)")
    LoginVO toLoginVO(User user, String accessToken, String tokenType, Long expiresIn);

    UserVO toUserVO(User user);
}
