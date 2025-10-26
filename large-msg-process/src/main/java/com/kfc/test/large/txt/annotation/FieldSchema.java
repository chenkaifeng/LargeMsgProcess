package com.kfc.test.large.txt.annotation;

import java.lang.annotation.*;

/**
 * 字段级注解：描述单个字段的解析规则（索引、长度等）
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldSchema {
    // 字段索引（分隔符解析：第几个字段；定长解析：字段顺序）
    int index();

    // 字段长度（仅当类注解type=FIXED_LENGTH时生效）
    int length() default 0;

    // 是否忽略该字段
    boolean ignore() default false;
}