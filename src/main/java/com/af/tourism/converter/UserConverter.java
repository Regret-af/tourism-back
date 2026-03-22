package com.af.tourism.converter;

import com.af.tourism.pojo.entity.User;
import com.af.tourism.pojo.vo.UserPublicVO;
import org.mapstruct.Mapper;

/**
 * 用户相关对象转换器
 */
@Mapper(componentModel = "spring")
public interface UserConverter {

    UserPublicVO toUserPublicVO(User user);
}
