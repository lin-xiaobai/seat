package com.example.springboot.config.interceptor;

import java.lang.annotation.*;
//为了标记需要进行某种授权访问控制的方法。
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthAccess {
}
