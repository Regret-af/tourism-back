package com.af.tourism.storage;

import lombok.Data;

/**
 * 对象存储上传结果。
 */
@Data
public class StorageUploadResult {

    private String storageProvider;
    private String bucketName;
    private String region;
    private String objectKey;
    private String fileUrl;
}
