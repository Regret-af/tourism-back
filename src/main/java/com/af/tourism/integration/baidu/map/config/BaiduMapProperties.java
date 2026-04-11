package com.af.tourism.integration.baidu.map.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.baidu.map")
public class BaiduMapProperties {

    /**
     * API地址
     */
    private String baseUrl = "https://api.map.baidu.com";

    /**
     * Api Key
     */
    private String ak;
}

