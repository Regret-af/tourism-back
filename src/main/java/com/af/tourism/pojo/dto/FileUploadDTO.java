package com.af.tourism.pojo.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 文件上传请求。
 */
@Data
public class FileUploadDTO {

    @NotNull(message = "file不能为空")
    private MultipartFile file;

    @NotBlank(message = "bizType不能为空")
    private String bizType;
}
