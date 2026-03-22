package com.af.tourism.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 上传文件元数据实体。
 */
@Data
@TableName("uploaded_files")
public class UploadedFile {

    @TableId
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("biz_type")
    private String bizType;

    @TableField("storage_provider")
    private String storageProvider;

    @TableField("bucket_name")
    private String bucketName;

    private String region;

    @TableField("object_key")
    private String objectKey;

    @TableField("file_url")
    private String fileUrl;

    @TableField("original_name")
    private String originalName;

    @TableField("mime_type")
    private String mimeType;

    @TableField("file_size")
    private Long fileSize;

    private Integer status;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
