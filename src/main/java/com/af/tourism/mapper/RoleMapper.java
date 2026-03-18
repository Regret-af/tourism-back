package com.af.tourism.mapper;

import com.af.tourism.pojo.entity.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色表 Mapper。
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 通过角色码查询角色
     * @param roleCode 角色码
     * @return 角色信息
     */
    Role selectByRoleCode(@Param("roleCode") String roleCode);

    /**
     * 查询用户角色列表
     * @param userId 用户id
     * @return 用户角色列表
     */
    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);
}
