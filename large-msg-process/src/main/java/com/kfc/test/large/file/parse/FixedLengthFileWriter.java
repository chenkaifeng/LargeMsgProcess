package com.kfc.test.large.file.parse;

import com.kfc.test.large.file.annotation.FixedLengthField;
import com.kfc.test.large.file.annotation.FixedLengthType;
import com.kfc.test.large.file.enums.FileCharSetEnum;
import com.kfc.test.large.file.enums.FixedLengthTypeEnum;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;

public class FixedLengthFileWriter {

    /**
     * 将 Java Bean 转换为定长字符串
     *
     * @param bean Java Bean 实例
     * @param <T>  Java Bean 类型
     * @return 定长字符串
     */
    public static <T> String convertToFixedLengthString(T bean) {
        StringBuilder line = new StringBuilder();

        FixedLengthType fixedLengthType = bean.getClass().getAnnotation(FixedLengthType.class);
        FixedLengthTypeEnum fixedLengthTypeEnum = fixedLengthType.type();
        FileCharSetEnum fileCharSetEnum = fixedLengthType.charset();


        Field[] fields = bean.getClass().getDeclaredFields(); // 获取所有字段
        for (Field field : fields) {
            if (field.isAnnotationPresent(FixedLengthField.class)) {
                FixedLengthField annotation = field.getAnnotation(FixedLengthField.class);
                int length = annotation.length();
                boolean trim = annotation.trim();

                try {
                    field.setAccessible(true); // 允许访问私有字段
                    Object value = field.get(bean); // 获取字段值
                    String fieldValue = value != null ? value.toString() : ""; // 处理空值

                    line.append(padRight(fieldValue, length, fixedLengthTypeEnum, fileCharSetEnum)); // 填充字段值

                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to access field: " + field.getName(), e);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException("UnsupportedEncoding: " + field.getName(), e);
                }
            }
        }

        return line.toString();
    }

    /**
     * 右对齐填充字符串
     *
     * @param str    原始字符串
     * @param length 目标长度
     * @param fileCharSetEnum
     * @param fixedLengthTypeEnum
     * @return 填充后的字符串
     */
    private static String padRight(String str, int length, FixedLengthTypeEnum fixedLengthTypeEnum, FileCharSetEnum fileCharSetEnum) throws UnsupportedEncodingException {
        if(fixedLengthTypeEnum == FixedLengthTypeEnum.CHAR){
            return padRightByCharLenth(str, length);
        }
        return padRightByByteLenth(str, length, fileCharSetEnum.getCode());
    }

    /**
     * 右对齐填充字符串-字符方式
     *
     * @param str    原始字符串
     * @param length 目标长度
     * @return 填充后的字符串
     */
    private static String padRightByCharLenth(String str, int length) {
        if (str == null) {
            str = "";
        }
        str = str.trim();

        return StringUtils.rightPad(str, length, " ");
    }

    /**
     * 右对齐填充字符串-字节方式
     *
     * @param str    原始字符串
     * @param length 目标长度
     * @param charset 编码
     * @return 填充后的字符串
     */
    private static String padRightByByteLenth(String str, int length, String charset) throws UnsupportedEncodingException {
        if (str == null) {
            str = "";
        }
        str = str.trim();

        // 按字节计算
        int byteLength = str.getBytes(charset).length;
        if (byteLength > str.length()) {
            length = length - (byteLength - str.length());
        }
        str = StringUtils.rightPad(str, length, " ");
        return str;
    }
}
