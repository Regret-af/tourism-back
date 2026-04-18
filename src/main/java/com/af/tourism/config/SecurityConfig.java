package com.af.tourism.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 基础配置。
 * 当前阶段仅接入安全过滤器链，不改变现有 securitylite 的鉴权行为。
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 前后端分离 + JWT 场景，不使用服务端 Session
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 当前仍沿用自定义登录接口，不启用 Spring Security 默认登录页和 Basic 认证
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .rememberMe(AbstractHttpConfigurer::disable)
                // 第一步先全部放行，后续再逐步迁移到 Security 的认证授权体系
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll()
                )
                .anonymous(Customizer.withDefaults());

        return http.build();
    }
}
