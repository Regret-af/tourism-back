package com.af.tourism.config;

import com.af.tourism.config.converter.AttractionCategoryStatusConverter;
import com.af.tourism.config.converter.AttractionStatusConverter;
import com.af.tourism.config.converter.DiaryCommentStatusConverter;
import com.af.tourism.config.converter.DiaryDeletedStatusConverter;
import com.af.tourism.config.converter.DiaryStatusConverter;
import com.af.tourism.config.converter.DiaryTopStatusConverter;
import com.af.tourism.config.converter.DiaryVisibilityConverter;
import com.af.tourism.securitylite.AdminAuthInterceptor;
import com.af.tourism.securitylite.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置，注册登录拦截器。
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final AdminAuthInterceptor adminAuthInterceptor;
    private final AttractionCategoryStatusConverter attractionCategoryStatusConverter;
    private final AttractionStatusConverter attractionStatusConverter;
    private final DiaryStatusConverter diaryStatusConverter;
    private final DiaryDeletedStatusConverter diaryDeletedStatusConverter;
    private final DiaryVisibilityConverter diaryVisibilityConverter;
    private final DiaryTopStatusConverter diaryTopStatusConverter;
    private final DiaryCommentStatusConverter diaryCommentStatusConverter;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(attractionCategoryStatusConverter);
        registry.addConverter(attractionStatusConverter);
        registry.addConverter(diaryStatusConverter);
        registry.addConverter(diaryDeletedStatusConverter);
        registry.addConverter(diaryVisibilityConverter);
        registry.addConverter(diaryTopStatusConverter);
        registry.addConverter(diaryCommentStatusConverter);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/v1/ping", "/auth/**", "/api/v1/auth/**", "/swagger-resources/**", "/swagger-ui/**", "/v3/api-docs/**");

        registry.addInterceptor(adminAuthInterceptor)
                .addPathPatterns("/api/v1/admin/**")
                .excludePathPatterns("/api/v1/admin/auth/login", "/swagger-resources/**", "/swagger-ui/**", "/v3/api-docs/**");
    }
}
