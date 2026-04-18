package com.af.tourism.security;

import com.af.tourism.mapper.RoleMapper;
import com.af.tourism.mapper.UserMapper;
import com.af.tourism.pojo.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Spring Security 用户加载服务。
 */
@Service
@RequiredArgsConstructor
public class SecurityUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;

    /**
     * 按登录标识加载用户。
     * @param username 登录标识
     * @return 安全用户对象
     * @throws UsernameNotFoundException 未找到异常
     */
    @Override
    @Transactional(readOnly = true)
    public SecurityUser loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1.校验参数
        if (!StringUtils.hasText(username)) {
            throw new UsernameNotFoundException("登录标识不能为空");
        }

        // 2.查询用户实体
        User user = userMapper.selectByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        // 3.封装并返回
        return buildSecurityUser(user);
    }

    /**
     * 按用户 ID 加载用户。
     * @param userId 用户 id
     * @return 安全用户实体
     */
    @Transactional(readOnly = true)
    public SecurityUser loadUserByUserId(Long userId) {
        // 1.校验参数
        if (userId == null) {
            throw new UsernameNotFoundException("用户ID不能为空");
        }

        // 2.查询用户实体
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        // 3.封装并返回
        return buildSecurityUser(user);
    }

    /**
     * 构建安全用户对象
     * @param user 用户实体
     * @return 安全用户对象
     */
    private SecurityUser buildSecurityUser(User user) {
        List<String> roleCodes = roleMapper.selectRoleCodesByUserId(user.getId());
        return SecurityUser.from(user, roleCodes);
    }
}
