package com.af.tourism.pojo.vo.admin;

import lombok.Data;

/**
 * 管理端用户下拉选项
 */
@Data
public class UserOptionForAdminVO {

    private Long id;

    private String nickname;

    private String email;

    private String avatarUrl;
}
