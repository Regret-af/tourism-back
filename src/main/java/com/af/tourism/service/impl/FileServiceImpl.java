package com.af.tourism.service.impl;

import com.af.tourism.common.ErrorCode;
import com.af.tourism.common.enums.FileBizType;
import com.af.tourism.config.FileUploadProperties;
import com.af.tourism.converter.FileConverter;
import com.af.tourism.exception.BusinessException;
import com.af.tourism.mapper.UploadedFileMapper;
import com.af.tourism.pojo.dto.app.FileUploadDTO;
import com.af.tourism.pojo.entity.UploadedFile;
import com.af.tourism.pojo.vo.app.FileUploadVO;
import com.af.tourism.service.FileService;
import com.af.tourism.storage.ObjectStorageService;
import com.af.tourism.storage.StorageUploadResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * 文件服务实现。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceImpl implements FileService {

    private final UploadedFileMapper uploadedFileMapper;
    private final ObjectStorageService objectStorageService;
    private final FileUploadProperties fileUploadProperties;

    private final FileConverter fileConverter;

    private final Tika tika = new Tika();

    /**
     * 文件上传接口
     * @param userId 用户 id
     * @param request 文件与文件类型
     * @return 上传文件相关信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileUploadVO uploadFile(Long userId, FileUploadDTO request) {
        // 1.获取文件并进行校验
        MultipartFile file = request.getFile();
        // 1.1.校验文件是否为空
        if (file == null || file.isEmpty()) {
            log.warn("上传文件失败，文件为空，userId={}", userId);
            throw new BusinessException(ErrorCode.PARAM_INVALID, "上传文件不能为空");
        }

        // 1.2.校验业务类型是否合规并转换为枚举类型
        FileBizType bizType = FileBizType.fromCode(request.getBizType());
        // 1.3.校验文件大小是否合规
        validateFileSize(file);
        // 1.4.解析文件真实类型，校验文件类型是否合规
        String mimeType = detectMimeType(file);
        validateMimeType(mimeType);

        // 2.上传文件
        StorageUploadResult uploadResult = objectStorageService.upload(file, bizType, userId);

        // 3.将上传文件操作记录入库
        UploadedFile uploadedFile = fileConverter.toUploadedFile(uploadResult);
        uploadedFile.setUserId(userId);
        uploadedFile.setBizType(bizType.getCode());
        uploadedFile.setOriginalName(file.getOriginalFilename());
        uploadedFile.setMimeType(mimeType);
        uploadedFile.setFileSize(file.getSize());
        uploadedFile.setStatus(1);
        uploadedFile.setCreatedAt(LocalDateTime.now());
        uploadedFileMapper.insert(uploadedFile);

        return fileConverter.toFileUploadVO(uploadedFile);
    }

    /**
     * 校验文件大小是否合规
     * @param file 文件
     */
    private void validateFileSize(MultipartFile file) {
        if (file.getSize() > fileUploadProperties.getMaxFileSize()) {
            log.warn("上传失败，文件大小超出限制，fileSize={}", file.getSize());
            throw new BusinessException(ErrorCode.PARAM_INVALID, "文件大小超出限制");
        }
    }

    /**
     * 校验文件类型是否合规
     * @param mimeType 文件类型
     */
    private void validateMimeType(String mimeType) {
        if (!fileUploadProperties.getAllowedMimeTypes().contains(mimeType)) {
            log.warn("上传失败，不支持上传该文件类型，mimeType={}", mimeType);
            throw new BusinessException(ErrorCode.PARAM_INVALID, "文件类型不支持");
        }
    }

    /**
     * 检查文件真实类型
     * @param file 文件
     * @return 文件真实类型
     */
    private String detectMimeType(MultipartFile file) {
        try {
            return tika.detect(file.getInputStream(), file.getOriginalFilename());
        } catch (IOException ex) {
            log.warn("上传失败，文件解析失败");
            throw new BusinessException(ErrorCode.FILE_UPLOAD_ERROR, "文件解析失败");
        }
    }
}
