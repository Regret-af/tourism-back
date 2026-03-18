package com.af.tourism.mapper;

import com.af.tourism.pojo.entity.UserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户角色关联表 Mapper。
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

    UserRole selectByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);
}
