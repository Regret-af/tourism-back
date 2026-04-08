package com.af.tourism.mapper;

import com.af.tourism.pojo.dto.admin.UserQueryDTO;
import com.af.tourism.pojo.entity.User;
import com.af.tourism.pojo.vo.admin.UserForAdminVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户表 Mapper。
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 通过 Email 查询用户
     * @param email  邮箱
     * @return 用户实体
     */
    User selectByEmail(@Param("email") String email);

    /**
     * 通过用户名查询用户
     * @param username 用户名
     * @return 用户实体
     */
    User selectByUsername(@Param("username") String username);

    /**
     * 查询用户列表
     * @param queryDTO 查询条件
     * @return 符合条件的用户列表
     */
    List<UserForAdminVO> selectUserList(@Param("queryDTO") UserQueryDTO queryDTO);
}
