package com.campus.safety.common.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

    /** 模块名称 */
    String module() default "";

    /** 操作描述 */
    String description() default "";
}
