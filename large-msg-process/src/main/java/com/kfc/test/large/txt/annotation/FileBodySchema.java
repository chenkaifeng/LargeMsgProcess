package com.kfc.test.large.txt.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FileBodySchema {
    String charset() default "UTF-8";
    ParseType type();
    String delimiter() default "";
    Class<?> headerClass() default void.class;

    enum ParseType {
        DELIMITER, FIXED_LENGTH
    }
}