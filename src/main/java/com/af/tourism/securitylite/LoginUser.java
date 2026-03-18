package com.af.tourism.securitylite;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 当前登录用户上下文对象。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser {

    private Long userId;
}
