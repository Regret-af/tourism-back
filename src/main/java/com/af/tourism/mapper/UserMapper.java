package com.af.tourism.mapper;

import com.af.tourism.pojo.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户表 Mapper。
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    User selectByAccount(@Param("account") String account);
}
