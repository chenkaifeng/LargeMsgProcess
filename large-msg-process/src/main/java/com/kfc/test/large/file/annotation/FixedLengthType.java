package com.kfc.test.large.file.annotation;

import com.kfc.test.large.file.enums.FileCharSetEnum;
import com.kfc.test.large.file.enums.FixedLengthTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE) // 注解作用于类
@Retention(RetentionPolicy.RUNTIME) // 注解在运行时可用
public @interface FixedLengthType {

    // 定长类型
    FixedLengthTypeEnum type() default FixedLengthTypeEnum.BYTE;

    // 文件编码
    FileCharSetEnum charset() default FileCharSetEnum.GBK;
}
