package org.example.annotation;

import java.lang.annotation.*;

/**
 * 需要身份验证的注解
 * 标记在需要登录才能访问的接口上
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireAuth {
    /**
     * 是否需要认证，默认true
     */
    boolean required() default true;
}

