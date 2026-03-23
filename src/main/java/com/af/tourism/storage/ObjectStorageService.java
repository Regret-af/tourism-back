package com.af.tourism.storage;

import com.af.tourism.common.enums.FileBizType;
import org.springframework.web.multipart.MultipartFile;

/**
 * 对象存储服务骨架。
 */
public interface ObjectStorageService {

    /**
     * 对象存储，上传文件
     * @param file 文件
     * @param bizType 业务类型
     * @param userId 用户 id
     * @return 上传文件信息
     */
    StorageUploadResult upload(MultipartFile file, FileBizType bizType, Long userId);
}
