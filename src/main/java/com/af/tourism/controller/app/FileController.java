package com.af.tourism.controller.app;

import com.af.tourism.annotation.OperationLogRecord;
import com.af.tourism.common.ApiResponse;
import com.af.tourism.common.enums.OperationLogAction;
import com.af.tourism.common.enums.OperationLogModule;
import com.af.tourism.pojo.dto.app.FileUploadDTO;
import com.af.tourism.pojo.vo.app.FileUploadVO;
import com.af.tourism.security.util.SecurityUtils;
import com.af.tourism.service.app.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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
@PreAuthorize("isAuthenticated()")
public class FileController {

    private final FileService fileService;

    /**
     * 文件上传接口
     * @param request 文件与文件类型
     * @return 上传文件相关信息
     */
    @PostMapping("/upload")
    @OperationLogRecord(module = OperationLogModule.FILE, action = OperationLogAction.UPLOAD_FILE, description = "上传文件", bizIdField = "data.fileId")
    public ApiResponse<FileUploadVO> uploadFile(@Valid @ModelAttribute FileUploadDTO request) {
        // 获取用户id，若 id 为空，直接抛出异常
        Long userId = SecurityUtils.requireCurrentUserId();
        return ApiResponse.ok(fileService.uploadFile(userId, request));
    }
}
