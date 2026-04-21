package com.af.tourism.service.cache;

import com.af.tourism.common.constants.RedisTtlConstants;
import com.af.tourism.pojo.entity.User;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthCacheSupport {

    private static final TypeReference<List<String>> STRING_LIST_TYPE = new TypeReference<List<String>>() {
    };

    private final CacheClient cacheClient;
    private final CacheKeySupport cacheKeySupport;

    /**
     * 获取用户信息
     * @param userId 用户 id
     * @param dbLoader 函数式接口，数据库查询用户信息
     * @return 用户实体
     */
    public User getUser(Long userId, Supplier<User> dbLoader) {
        // 1.构建 key
        String cacheKey = cacheKeySupport.buildAuthUserKey(userId);

        // 2.尝试从缓存中获取用户信息
        try {
            User cachedUser = cacheClient.get(cacheKey, User.class);
            if (cachedUser != null) {
                return cachedUser;
            }
        } catch (Exception ex) {
            log.warn("读取用户资料缓存失败，回源数据库，cacheKey={}", cacheKey, ex);
        }

        // 3.回源数据库查询用户信息
        User user = dbLoader.get();
        if (user == null) {
            return null;
        }

        // 4.写入用户资料缓存
        try {
            cacheClient.set(cacheKey, user, RedisTtlConstants.AUTH);
        } catch (Exception ex) {
            log.warn("写入用户资料缓存失败，cacheKey={}", cacheKey, ex);
        }

        return user;
    }

    /**
     * 获取角色信息
     * @param userId 用户 id
     * @param dbLoader 函数式接口，数据库查询角色信息
     * @return 角色列表
     */
    public List<String> getRoleCodes(Long userId, Supplier<List<String>> dbLoader) {
        // 1.构建 key
        String cacheKey = cacheKeySupport.buildAuthRoleCodesKey(userId);

        // 2.尝试从缓存中获取角色信息
        try {
            List<String> cachedRoleCodes = cacheClient.get(cacheKey, STRING_LIST_TYPE);
            if (cachedRoleCodes != null) {
                return cachedRoleCodes;
            }
        } catch (Exception ex) {
            log.warn("读取用户角色缓存失败，回源数据库，cacheKey={}", cacheKey, ex);
        }

        // 3.回源数据库查询角色列表
        List<String> roleCodes = dbLoader.get();
        if (roleCodes == null) {
            roleCodes = Collections.emptyList();
        }

        // 4.写入角色缓存
        try {
            cacheClient.set(cacheKey, roleCodes, RedisTtlConstants.AUTH);
        } catch (Exception ex) {
            log.warn("写入用户角色缓存失败，cacheKey={}", cacheKey, ex);
        }

        return roleCodes;
    }
}
