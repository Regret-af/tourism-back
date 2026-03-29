package com.af.tourism.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

/**
 * COS 存储骨架配置。
 */
@Component
@ConfigurationProperties(prefix = "app.cos")
@Validated
@Data
public class CosStorageProperties {

    private String provider = "COS";

    @NotBlank
    private String secretId;

    @NotBlank
    private String secretKey;

    @NotBlank
    private String bucketName;

    @NotBlank
    private String region;

    private String baseUrl;
}
