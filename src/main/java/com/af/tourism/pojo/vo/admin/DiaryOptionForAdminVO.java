package com.af.tourism.pojo.vo.admin;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理端日记下拉选项
 */
@Data
public class DiaryOptionForAdminVO {

    private Long id;

    private String title;

    private String coverUrl;

    private DiaryAuthorForAdminVO author;
}
