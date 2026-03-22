package com.af.tourism.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * COS 存储骨架配置。
 */
@Component
@ConfigurationProperties(prefix = "app.cos")
@Data
public class CosStorageProperties {

    private String provider = "COS";

    private String bucketName = "demo-bucket";

    private String region = "ap-guangzhou";

    private String baseUrl = "https://demo-bucket.cos.ap-guangzhou.myqcloud.com";
}
