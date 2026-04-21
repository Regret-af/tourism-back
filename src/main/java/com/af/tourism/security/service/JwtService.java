package com.af.tourism.security.service;

import cn.hutool.crypto.digest.DigestUtil;
import com.af.tourism.common.constants.AuthConstants;
import com.af.tourism.common.constants.RedisKeyConstants;
import com.af.tourism.exception.UnauthorizedException;
import com.af.tourism.service.cache.CacheClient;
import com.af.tourism.service.cache.CacheKeyBuilder;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;

/**
 * JWT 服务，负责生成、解析和黑名单控制。
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    @Value("${app.jwt.secret:change-me}")
    private String secret;

    @Value("${app.jwt.expire-seconds:86400}")
    private long expireSeconds;

    private final CacheClient cacheClient;
    private final CacheKeyBuilder cacheKeyBuilder;

    private SecretKey secretKey;

    /**
     * 初始化完成生成密钥
     */
    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(DigestUtil.sha256(secret));
    }

    /**
     * 为用户生成 JWT 令牌
     * @param userId 用户 id
     * @return JWT 令牌
     */
    public String generateToken(Long userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expireSeconds * 1000);
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析 JWT 令牌
     * @param token 令牌
     * @return 当前登录用户 id
     */
    public Long parseUserId(String token) {
        Claims claims = parseClaims(token);
        validateNotBlacklisted(token);
        return Long.valueOf(claims.getSubject());
    }

    /**
     * 将 token 加入 Redis 黑名单，过期时间与 token 剩余有效期一致
     * @param authorizationHeader Authorization 请求头
     */
    public void blacklist(String authorizationHeader) {
        String token = extractToken(authorizationHeader);
        Claims claims = parseClaims(token);

        long remainingMillis = claims.getExpiration().getTime() - System.currentTimeMillis();
        if (remainingMillis <= 0) {
            return;
        }

        String blacklistKey = buildBlacklistKey(token);
        cacheClient.set(blacklistKey, "1", Duration.ofMillis(remainingMillis));
    }

    /**
     * 获取令牌过期时间。
     * @return 过期时间
     */
    public long getExpireSeconds() {
        return expireSeconds;
    }

    /**
     * 验证 token 是否在黑名单上
     * @param token token
     */
    private void validateNotBlacklisted(String token) {
        // 1.缓存黑名单 key
        String blacklistKey = buildBlacklistKey(token);
        // 2.查询缓存黑名单
        String marker = cacheClient.get(blacklistKey, String.class);
        if (marker != null) {
            throw new UnauthorizedException("Token已失效");
        }
    }

    /**
     * 解析 token
     * @param token token
     * @return
     */
    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception ex) {
            throw new UnauthorizedException("Token无效");
        }
    }

    /**
     * 通过请求头解析 token
     * @param authorizationHeader 请求头
     * @return token
     */
    private String extractToken(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith(AuthConstants.BEARER_PREFIX)) {
            throw new UnauthorizedException("Authorization头格式错误");
        }

        String token = authorizationHeader.substring(AuthConstants.BEARER_PREFIX.length()).trim();
        if (!StringUtils.hasText(token)) {
            throw new UnauthorizedException("Token不能为空");
        }
        return token;
    }

    /**
     * 构建缓存黑名单 key
     * @param token token
     * @return 缓存黑名单 key
     */
    private String buildBlacklistKey(String token) {
        return cacheKeyBuilder.build(RedisKeyConstants.AUTH_TOKEN_BLACKLIST, DigestUtil.sha256Hex(token));
    }

}
