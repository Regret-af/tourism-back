package com.af.tourism.service.app;

import com.af.tourism.pojo.dto.app.FileUploadDTO;
import com.af.tourism.pojo.vo.app.FileUploadVO;

public interface FileService {

    /**
     * 文件上传接口
     * @param userId 用户 id
     * @param request 文件与文件类型
     * @return 上传文件相关信息
     */
    FileUploadVO uploadFile(Long userId, FileUploadDTO request);
}
