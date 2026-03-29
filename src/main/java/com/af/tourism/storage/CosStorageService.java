package com.af.tourism.storage;

import com.af.tourism.common.ErrorCode;
import com.af.tourism.common.enums.FileBizType;
import com.af.tourism.config.CosStorageProperties;
import com.af.tourism.converter.FileConverter;
import com.af.tourism.exception.BusinessException;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * COS 存储服务。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CosStorageService implements ObjectStorageService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final FileConverter fileConverter;

    private final CosStorageProperties cosStorageProperties;
    private final COSClient cosClient;

    /**
     * 对象存储，上传文件
     * @param file 文件
     * @param bizType 业务类型
     * @param userId 用户 id
     * @return 上传文件信息
     */
    @Override
    public StorageUploadResult upload(MultipartFile file, FileBizType bizType, Long userId) {
        // 1.构建上传文件名
        String objectKey = buildObjectKey(file.getOriginalFilename(), bizType, userId);

        // 2.进行文件上传
        try (InputStream inputStream = file.getInputStream()) {
            // 2.1.设置文件元数据
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            if (StringUtils.hasText(file.getContentType())) {
                metadata.setContentType(file.getContentType());
            }

            // 2.2.进行上传请求封装
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    cosStorageProperties.getBucketName(),
                    objectKey,
                    inputStream,
                    metadata
            );

            // 2.3.进行文件上传
            cosClient.putObject(putObjectRequest);
        } catch (IOException ex) {
            log.error("COS上传失败，读取文件流异常，objectKey={}", objectKey, ex);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_ERROR, "文件读取失败");
        } catch (CosServiceException ex) {
            log.error("COS上传失败，服务端返回异常，objectKey={}", objectKey, ex);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_ERROR, "文件上传失败");
        } catch (CosClientException ex) {
            log.error("COS上传失败，客户端请求异常，objectKey={}", objectKey, ex);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_ERROR, "文件上传失败");
        }

        // 3.封装返回请求
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
    private String buildObjectKey(String originalName, FileBizType bizType, Long userId) {
        String extension = "";
        if (StringUtils.hasText(originalName) && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf('.'));
        }

        return bizType.getCode() + "/" + userId + "/" + LocalDate.now().format(DATE_FORMATTER) + "/" + UUID.randomUUID() + extension;
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