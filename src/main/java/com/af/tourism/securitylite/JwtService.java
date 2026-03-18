package com.af.tourism.securitylite;

import com.af.tourism.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;

/**
 * 轻量 JWT 服务，仅负责生成和解析当前登录用户标识。
 */
@Component
public class JwtService {

    @Value("${app.jwt.secret:change-me}")
    private String secret;

    @Value("${app.jwt.expire-seconds:86400}")
    private long expireSeconds;

    private SecretKey secretKey;

    /**
     * 初始胡完成生成密钥
     */
    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(digestSecret(secret));
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
     * @return 当前登录用户上下文对象
     */
    public LoginUser parseLoginUser(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return new LoginUser(Long.valueOf(claims.getSubject()));
        } catch (Exception ex) {
            throw new UnauthorizedException("Token无效");
        }
    }

    /**
     * 解析用户 id
     * @param token 令牌
     * @return 登录用户 id
     */
    public Long parseUserId(String token) {
        return parseLoginUser(token).getUserId();
    }

    /**
     * 获取令牌过期时间
     * @return 过期时间
     */
    public long getExpireSeconds() {
        return expireSeconds;
    }

    /**
     * 生成 JWT 签名所需的密钥材料
     * @param rawSecret 原始密钥字符串
     * @return 加密后密钥
     */
    private byte[] digestSecret(String rawSecret) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            return messageDigest.digest(rawSecret.getBytes(StandardCharsets.UTF_8));
        } catch (Exception ex) {
            throw new IllegalStateException("JWT密钥初始化失败", ex);
        }
    }
}
