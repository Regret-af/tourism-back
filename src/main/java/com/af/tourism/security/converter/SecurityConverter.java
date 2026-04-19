package com.af.tourism.security.converter;

import com.af.tourism.pojo.entity.User;
import com.af.tourism.security.model.SecurityUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SecurityConverter {

    @Mapping(source = "userId", target = "id")
    User toUser(SecurityUser securityUser);
}
