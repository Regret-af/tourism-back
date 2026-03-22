package com.af.tourism.mapper;

import com.af.tourism.pojo.entity.UploadedFile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 上传文件 Mapper。
 */
@Mapper
public interface UploadedFileMapper extends BaseMapper<UploadedFile> {

    /**
     * 通过文件名进行查询
     * @param objectKey 文件名
     * @return 上传文件记录
     */
    UploadedFile selectByObjectKey(@Param("objectKey") String objectKey);
}
