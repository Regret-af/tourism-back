package com.af.tourism.storage;

import com.af.tourism.config.CosStorageProperties;
import com.af.tourism.converter.FileConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * COS 存储服务骨架。
 */
@Service
@RequiredArgsConstructor
public class CosStorageService implements ObjectStorageService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final FileConverter fileConverter;

    private final CosStorageProperties cosStorageProperties;

    /**
     * 对象存储，上传文件
     * @param file 文件
     * @param bizType 业务类型
     * @param userId 用户 id
     * @return 上传文件信息
     */
    @Override
    public StorageUploadResult upload(MultipartFile file, String bizType, Long userId) {
        String objectKey = buildObjectKey(file.getOriginalFilename(), bizType, userId);

        // TODO: 对接 COS 执行上传操作

        StorageUploadResult result = fileConverter.toStorageUploadResult(cosStorageProperties);
        result.setObjectKey(objectKey);
        result.setFileUrl(buildFileUrl(objectKey));

        return result;
    }

    /**
     * 构建文件名
     * @param originalName 文件名
     * @param bizType 业务类型
     * @param userId 用户 id
     * @return 文件名
     */
    private String buildObjectKey(String originalName, String bizType, Long userId) {
        // 构建文件后缀
        String extension = "";
        if (StringUtils.hasText(originalName) && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf('.'));
        }

        return bizType + "/" + userId + "/" + LocalDate.now().format(DATE_FORMATTER) + "/" + UUID.randomUUID() + extension;
    }

    /**
     * 构建访问路径
     * @param objectKey 文件名
     * @return 访问路径
     */
    private String buildFileUrl(String objectKey) {
        String baseUrl = cosStorageProperties.getBaseUrl();
        if (!StringUtils.hasText(baseUrl)) {
            baseUrl = "https://" + cosStorageProperties.getBucketName() + ".cos." + cosStorageProperties.getRegion() + ".myqcloud.com";
        }
        if (baseUrl.endsWith("/")) {
            return baseUrl + objectKey;
        }
        return baseUrl + "/" + objectKey;
    }
}
