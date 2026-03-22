package com.af.tourism.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 文件上传基础配置。
 */
@Component
@ConfigurationProperties(prefix = "app.file-upload")
@Data
public class FileUploadProperties {

    private long maxFileSize = 10 * 1024 * 1024L;

    // 文件类型
    private List<String> allowedMimeTypes = Arrays.asList(
            "image/jpeg",
            "image/png",
            "image/webp",
            "image/gif"
    );

    // 业务类型
    private List<String> allowedBizTypes = Arrays.asList("avatar", "diary_image");
}
