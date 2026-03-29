package com.af.tourism.config;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CosConfig {

    private final CosStorageProperties cosStorageProperties;

    @Bean(destroyMethod = "shutdown")
    public COSClient cosClient() {
        COSCredentials credentials = new BasicCOSCredentials(
                cosStorageProperties.getSecretId(),
                cosStorageProperties.getSecretKey()
        );
        ClientConfig clientConfig = new ClientConfig(new Region(cosStorageProperties.getRegion()));
        return new COSClient(credentials, clientConfig);
    }
}
