package org.example.annotation;

import java.lang.annotation.*;

/**
 * 允许游客访问的注解
 * 标记在不需要登录就能访问的接口上（如登录、注册、查看笔记等）
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GuestAllowed {
}

