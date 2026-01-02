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
import java.util.Date;

/**
 * 精简版 JWT 服务，仅提供生成/解析 userId。
 */
@Component
public class JwtService {

    @Value("${app.jwt.secret:change-me}")
    private String secret;

    @Value("${app.jwt.expire-seconds:86400}")
    private long expireSeconds;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        // 使用 HMAC SHA 生成密钥；生产请使用安全随机字符串。
        secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

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

    public Long parseUserId(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return Long.valueOf(claims.getSubject());
        } catch (Exception ex) {
            throw new UnauthorizedException("无效的Token");
        }
    }
}
