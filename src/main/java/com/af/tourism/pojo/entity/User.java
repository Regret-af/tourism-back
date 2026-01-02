package com.af.tourism.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户表实体，仅示例字段。
 */
@Data
@TableName("users")
public class User {
    @TableId
    private Long id;
    private String username;
    private String email;
    private Integer status;
}
