package com.af.tourism.integration.qweather.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.weather.qweather")
public class QWeatherProperties {

    /**
     * API地址
     */
    private String baseUrl = "https://devapi.qweather.com";

    /**
     * token
     */
    private String token;

}
