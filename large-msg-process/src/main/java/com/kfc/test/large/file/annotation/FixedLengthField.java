package com.kfc.test.large.file.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD) // 注解作用于字段
@Retention(RetentionPolicy.RUNTIME) // 注解在运行时可用
public @interface FixedLengthField {

    // 字段的长度
    int length();

    // 是否去除空白字符
    boolean trim() default true;
}
