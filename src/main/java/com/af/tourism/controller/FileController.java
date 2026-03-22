package com.af.tourism.controller;

import com.af.tourism.annotation.OperationLogRecord;
import com.af.tourism.common.ApiResponse;
import com.af.tourism.pojo.dto.FileUploadDTO;
import com.af.tourism.pojo.vo.FileUploadVO;
import com.af.tourism.securitylite.AuthContext;
import com.af.tourism.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 文件上传接口。
 */
@Validated
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    /**
     * 文件上传接口
     * @param request 文件与文件类型
     * @return 上传文件相关信息
     */
    @PostMapping("/upload")
    @OperationLogRecord(module = "FILE", action = "UPLOAD_FILE", description = "上传文件", bizIdField = "data.fileId")
    public ApiResponse<FileUploadVO> uploadFile(@Valid @ModelAttribute FileUploadDTO request) {
        // 获取用户id，若 id 为空，直接抛出异常
        Long userId = AuthContext.requireCurrentUserId();
        return ApiResponse.ok(fileService.uploadFile(userId, request));
    }
}
