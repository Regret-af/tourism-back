package com.af.tourism.converter;

import com.af.tourism.config.CosStorageProperties;
import com.af.tourism.pojo.entity.UploadedFile;
import com.af.tourism.pojo.vo.app.FileUploadVO;
import com.af.tourism.storage.StorageUploadResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 文件相关对象转换器
 */
@Mapper(componentModel = "spring")
public interface FileConverter {

    @Mapping(source = "provider", target = "storageProvider")
    StorageUploadResult toStorageUploadResult(CosStorageProperties cosStorageProperties);

    UploadedFile toUploadedFile(StorageUploadResult storageUploadResult);

    @Mapping(source = "id", target = "fileId")
    FileUploadVO toFileUploadVO(UploadedFile uploadedFile);
}
