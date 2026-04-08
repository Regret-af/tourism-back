package com.af.tourism.pojo.vo.app;

import lombok.Data;

/**
 * 注册响应。
 */
@Data
public class RegisterVO {

    private Long id;
    private String email;
    private String username;
    private String nickname;
}
