package com.af.tourism.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志记录注解。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationLogRecord {

    // 模块
    String module();

    // 操作
    String action();

    // 操作描述
    String description();

    // 操作用户 id
    String userIdField() default "";

    // 关联业务 id
    String bizIdField() default "";

    // 参数索引位置
    int bizIdArgIndex() default -1;
}
