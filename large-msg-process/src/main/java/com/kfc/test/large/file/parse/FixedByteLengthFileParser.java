package com.kfc.test.large.file.parse;

import com.kfc.test.large.file.annotation.FixedLengthField;
import com.kfc.test.large.file.annotation.FixedLengthType;
import com.kfc.test.large.file.enums.FileCharSetEnum;
import com.kfc.test.large.file.enums.FixedLengthTypeEnum;
import com.kfc.test.large.file.model.Person;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class FixedByteLengthFileParser {

    public static void main(String[] args) {
        String filePath = "path/to/your/fixed_length_file.txt"; // 替换为你的文件路径

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 解析每一行并映射到 Java Bean
                Person person = parseLine(line, Person.class);
                System.out.println(person); // 输出解析结果
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析一行数据并映射到 Java Bean
     *
     * @param line      当前行数据
     * @param beanClass Java Bean 的 Class 对象
     * @param <T>       Java Bean 类型
     * @return 解析后的 Java Bean
     */
    public static <T> T parseLine(String line, Class<T> beanClass) {
        try {

            FixedLengthType fixedLengthType = beanClass.getAnnotation(FixedLengthType.class);

            FixedLengthTypeEnum fixedLengthTypeEnum = fixedLengthType.type();
            FileCharSetEnum fileCharSetEnum = fixedLengthType.charset();

            T bean = beanClass.getDeclaredConstructor().newInstance();
            Field[] fields = beanClass.getDeclaredFields();


            int start = 0;
            for (Field field : fields) {
                if (field.isAnnotationPresent(FixedLengthField.class)) {
                    FixedLengthField annotation = field.getAnnotation(FixedLengthField.class);

                    int length = annotation.length();
                    boolean trim = annotation.trim();

                    // 截取字段值
                    String fieldValue = substring(line, start, length, fixedLengthTypeEnum, fileCharSetEnum);
                    start += length;
                    if (trim) {
                        fieldValue = fieldValue.trim();
                    }

                    // 设置字段值
                    field.setAccessible(true);
                    if (field.getType() == int.class || field.getType() == Integer.class) {
                        field.set(bean, Integer.parseInt(fieldValue));
                    } else {
                        field.set(bean, fieldValue);
                    }
                }
            }

            return bean;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse line to bean", e);
        }
    }

    /**
     * 截取字符串
     *
     * @param str        原始字符串
     * @param startIndex 起始索引
     * @param length     截取长度
     * @return 截取后的子字符串
     */
    private static String substring(String str, int startIndex, int length, FixedLengthTypeEnum fixedLengthTypeEnum, FileCharSetEnum fileCharSetEnum) throws UnsupportedEncodingException {
        if(fixedLengthTypeEnum == FixedLengthTypeEnum.CHAR){
            return substringByCharLength(str, startIndex, length);
        }
        return substringByByteLength(str, startIndex, length, fileCharSetEnum.getCode());
    }

    /**
     * 按字符数截取字符串
     *
     * @param str        原始字符串
     * @param startIndex 起始索引
     * @param length     截取长度
     * @return 截取后的子字符串
     */
    private static String substringByCharLength(String str, int startIndex, int length) {
        if (str == null || startIndex < 0 || length < 0 || startIndex + length > str.length()) {
            throw new IllegalArgumentException("Invalid substring parameters");
        }
        return str.substring(startIndex, startIndex + length);
    }

    /**
     * 按字节数截取字符串
     *
     * @param str        原始字符串
     * @param startIndex 起始索引
     * @param length     截取长度
     * @return 截取后的子字符串
     */
    private static String substringByByteLength(String str, int startIndex, int length, String charset) throws UnsupportedEncodingException {
        String value = "";
        byte[] data = str.getBytes(charset);
        if ((startIndex + length) > data.length) {
            length = data.length - startIndex;
            if (length <= 0) {
                return "";
            }
        }
        byte[] tem = new byte[length];
        for (int i = 0; i < length; i++) {
            tem[i] = data[startIndex + i];
        }
        value = new String(tem, charset);
        return value;
    }



}