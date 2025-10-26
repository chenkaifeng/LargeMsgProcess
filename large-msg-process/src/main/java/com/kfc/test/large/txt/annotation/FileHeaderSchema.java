package com.kfc.test.large.txt.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FileHeaderSchema {
    String charset() default "";
    ParseType type();
    String delimiter() default "";

    enum ParseType {
        DELIMITER, FIXED_LENGTH
    }
}