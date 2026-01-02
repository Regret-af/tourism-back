package com.af.tourism.service.impl;

import com.af.tourism.pojo.entity.User;
import com.af.tourism.mapper.UserMapper;
import com.af.tourism.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public User findById(Long id) {
        return userMapper.selectById(id);
    }
}
