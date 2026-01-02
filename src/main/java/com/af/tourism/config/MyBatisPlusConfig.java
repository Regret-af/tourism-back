package com.af.tourism.config;

import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置，不启用分页插件，保持与 PageHelper 并存。
 */
@Configuration
@MapperScan("com.af.tourism.mapper")
public class MyBatisPlusConfig {

    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> configuration.setMapUnderscoreToCamelCase(true);
    }
}
