package com.af.tourism.config;

import com.af.tourism.security.JwtAuthenticationFilter;
import com.af.tourism.security.RestAccessDeniedHandler;
import com.af.tourism.security.RestAuthenticationEntryPoint;
import com.af.tourism.security.SecurityUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 基础配置。
 * 当前阶段接管 JWT 认证、接口授权和统一安全异常响应。
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(SecurityUserDetailsService userDetailsService,
                                                               PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(DaoAuthenticationProvider daoAuthenticationProvider) {
        return new ProviderManager(daoAuthenticationProvider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   DaoAuthenticationProvider daoAuthenticationProvider,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter,
                                                   RestAuthenticationEntryPoint restAuthenticationEntryPoint,
                                                   RestAccessDeniedHandler restAccessDeniedHandler) throws Exception {
        http
                // 前后端分离 + JWT 场景，不使用服务端 Session
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 当前仍沿用自定义登录接口，不启用 Spring Security 默认登录页和 Basic 认证
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .rememberMe(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        .accessDeniedHandler(restAccessDeniedHandler)
                )
                .authorizeRequests(authorize -> authorize
                        .antMatchers(
                                "/",
                                "/index.html",
                                "/error",
                                "/swagger-resources/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .antMatchers(
                                HttpMethod.GET,
                                "/api/v1/ping",
                                "/api/v1/attraction-categories",
                                "/api/v1/attractions",
                                "/api/v1/attractions/*",
                                "/api/v1/attractions/*/weather",
                                "/api/v1/diary-categories/options",
                                "/api/v1/travel-diaries",
                                "/api/v1/travel-diaries/*",
                                "/api/v1/travel-diaries/*/more-from-author",
                                "/api/v1/travel-diaries/*/comments",
                                "/api/v1/users/*/travel-diaries"
                        ).permitAll()
                        .antMatchers(
                                HttpMethod.POST,
                                "/api/v1/auth/login",
                                "/api/v1/auth/register",
                                "/api/v1/admin/auth/login"
                        ).permitAll()
                        .antMatchers(
                                HttpMethod.POST,
                                "/api/v1/travel-diaries",
                                "/api/v1/travel-diaries/*/comments",
                                "/api/v1/travel-diaries/*/likes",
                                "/api/v1/travel-diaries/*/favorites"
                        ).authenticated()
                        .antMatchers(
                                HttpMethod.PUT,
                                "/api/v1/users/me/profile",
                                "/api/v1/users/me/password",
                                "/api/v1/travel-diaries/*"
                        ).authenticated()
                        .antMatchers(
                                HttpMethod.DELETE,
                                "/api/v1/travel-diaries/*",
                                "/api/v1/travel-diaries/*/likes",
                                "/api/v1/travel-diaries/*/favorites"
                        ).authenticated()
                        .antMatchers(
                                HttpMethod.PATCH,
                                "/api/v1/notifications/*/read"
                        ).authenticated()
                        .antMatchers(
                                "/api/v1/users/me",
                                "/api/v1/users/me/**",
                                "/api/v1/files/**",
                                "/api/v1/notifications/**"
                        ).authenticated()
                        .antMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .anyRequest().permitAll()
                )
                .authenticationProvider(daoAuthenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .anonymous(Customizer.withDefaults());

        return http.build();
    }
}
