package com.af.tourism.annotation;

import com.af.tourism.common.enums.OperationLogAction;
import com.af.tourism.common.enums.OperationLogModule;

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
    OperationLogModule module();

    // 操作
    OperationLogAction action();

    // 操作描述
    String description();

    // 操作用户 id
    String userIdField() default "";

    // 关联业务 id
    String bizIdField() default "";

    // 参数索引位置
    int bizIdArgIndex() default -1;
}
