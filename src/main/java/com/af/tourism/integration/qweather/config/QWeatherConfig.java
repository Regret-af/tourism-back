package com.af.tourism.integration.qweather.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(QWeatherProperties.class)
public class QWeatherConfig {
}
