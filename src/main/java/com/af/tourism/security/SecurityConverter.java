package com.af.tourism.security;

import com.af.tourism.pojo.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SecurityConverter {

    @Mapping(source = "userId", target = "id")
    User toUser(SecurityUser securityUser);
}
