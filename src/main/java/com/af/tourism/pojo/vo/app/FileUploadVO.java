package com.af.tourism.pojo.vo.app;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件上传响应。
 */
@Data
public class FileUploadVO {

    private Long fileId;
    private String bizType;
    private String fileUrl;
    private String originalName;
    private String mimeType;
    private Long fileSize;
    private String storageProvider;
    private LocalDateTime createdAt;
}
