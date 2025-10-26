package com.kfc.test.large.txt.parse;


import com.kfc.test.large.txt.annotation.FieldSchema;
import com.kfc.test.large.txt.annotation.FileBodySchema;
import com.kfc.test.large.txt.annotation.FileHeaderSchema;
import com.kfc.test.large.txt.exception.ParseException;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

public class AnnotationParser {

    public static <HEADER, BODY> ParseMetadataPair<HEADER, BODY> parse(Class<BODY> bodyClass) {
        FileBodySchema bodySchema = bodyClass.getAnnotation(FileBodySchema.class);
        if (bodySchema == null) {
            throw new IllegalArgumentException("文件体类" + bodyClass.getName() + "未标注@FileBodySchema注解");
        }

        Class<HEADER> headerClass = (Class<HEADER>) bodySchema.headerClass();
        ParseMetadata<HEADER> headerMetadata = null;
        if (headerClass != void.class) {
            headerMetadata = parseHeaderClass(headerClass, bodySchema.charset());
        }

        ParseMetadata<BODY> bodyMetadata = parseBodyClass(bodyClass, bodySchema);

        return new ParseMetadataPair<>(headerMetadata, bodyMetadata);
    }

    private static <HEADER> ParseMetadata<HEADER> parseHeaderClass(Class<HEADER> headerClass, String defaultCharset) {
        FileHeaderSchema headerSchema = headerClass.getAnnotation(FileHeaderSchema.class);
        if (headerSchema == null) {
            throw new IllegalArgumentException("文件头类" + headerClass.getName() + "未标注@FileHeaderSchema注解");
        }

        String charsetName = headerSchema.charset().isEmpty() ? defaultCharset : headerSchema.charset();
        if (!Charset.isSupported(charsetName)) {
            throw new IllegalArgumentException("文件头不支持的编码：" + charsetName);
        }
        Charset charset = Charset.forName(charsetName);

        List<Field> fields = getValidFields(headerClass);

        LineParser lineParser;
        FieldMapper<HEADER> fieldMapper;
        if (headerSchema.type() == FileHeaderSchema.ParseType.DELIMITER) {
            String delimiter = headerSchema.delimiter();
            if (delimiter.isEmpty()) {
                throw new IllegalArgumentException("文件头分隔符解析类型必须指定delimiter");
            }
            lineParser = new DelimiterParser(delimiter);
            fieldMapper = createDelimiterFieldMapper(fields);
        } else {
            lineParser = createFixedLengthParser(fields);
            fieldMapper = createFixedLengthFieldMapper(fields);
        }

        return new ParseMetadata<>(charset, lineParser, fieldMapper, headerClass);
    }

    private static <BODY> ParseMetadata<BODY> parseBodyClass(Class<BODY> bodyClass, FileBodySchema bodySchema) {
        String charsetName = bodySchema.charset();
        if (!Charset.isSupported(charsetName)) {
            throw new IllegalArgumentException("文件体不支持的编码：" + charsetName);
        }
        Charset charset = Charset.forName(charsetName);

        List<Field> fields = getValidFields(bodyClass);

        LineParser lineParser;
        FieldMapper<BODY> fieldMapper;
        if (bodySchema.type() == FileBodySchema.ParseType.DELIMITER) {
            String delimiter = bodySchema.delimiter();
            if (delimiter.isEmpty()) {
                throw new IllegalArgumentException("文件体分隔符解析类型必须指定delimiter");
            }
            lineParser = new DelimiterParser(delimiter);
            fieldMapper = createDelimiterFieldMapper(fields);
        } else {
            lineParser = createFixedLengthParser(fields);
            fieldMapper = createFixedLengthFieldMapper(fields);
        }

        return new ParseMetadata<>(charset, lineParser, fieldMapper, bodyClass);
    }

    private static List<Field> getValidFields(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> !field.getAnnotation(FieldSchema.class).ignore())
                .peek(field -> field.setAccessible(true))
                .collect(Collectors.toList());
    }

    private static LineParser createFixedLengthParser(List<Field> fields) {
        List<Integer> fieldLengths = fields.stream()
                .map(field -> {
                    FieldSchema schema = field.getAnnotation(FieldSchema.class);
                    if (schema.length() <= 0) {
                        throw new IllegalArgumentException("定长字段" + field.getName() + "必须指定length");
                    }
                    return schema.length();
                })
                .collect(Collectors.toList());
        return new FixedLengthParser(fieldLengths);
    }

    private static <T> FieldMapper<T> createDelimiterFieldMapper(List<Field> fields) {
        Map<Integer, Field> indexFieldMap = fields.stream()
                .collect(Collectors.toMap(
                        field -> field.getAnnotation(FieldSchema.class).index(),
                        field -> field,
                        (existing, replacement) -> {
                            throw new IllegalArgumentException("存在重复的index：" + existing.getAnnotation(FieldSchema.class).index());
                        }
                ));
        return (fieldsList, instance) -> {
            for (Map.Entry<Integer, Field> entry : indexFieldMap.entrySet()) {
                int index = entry.getKey();
                Field field = entry.getValue();
                if (index >= fieldsList.size()) {
                    throw new ParseException("字段index=" + index + "超出解析结果长度");
                }
                setFieldValue(field, instance, fieldsList.get(index));
            }
        };
    }

    private static <T> FieldMapper<T> createFixedLengthFieldMapper(List<Field> fields) {
        List<Field> sortedFields = new ArrayList<>(fields);
        sortedFields.sort(Comparator.comparingInt(f -> f.getAnnotation(FieldSchema.class).index()));
        return (fieldsList, instance) -> {
            if (fieldsList.size() != sortedFields.size()) {
                throw new ParseException("定长解析字段数与实体类字段数不匹配");
            }
            for (int i = 0; i < sortedFields.size(); i++) {
                Field field = sortedFields.get(i);
                setFieldValue(field, instance, fieldsList.get(i));
            }
        };
    }

    private static <T> void setFieldValue(Field field, T instance, String value) throws ParseException {
        try {
            Class<?> fieldType = field.getType();
            Object convertedValue;
            if (fieldType == String.class) {
                convertedValue = value;
            } else if (fieldType == int.class || fieldType == Integer.class) {
                convertedValue = Integer.parseInt(value.trim());
            } else if (fieldType == long.class || fieldType == Long.class) {
                convertedValue = Long.parseLong(value.trim());
            } else if (fieldType == double.class || fieldType == Double.class) {
                convertedValue = Double.parseDouble(value.trim());
            } else if (fieldType == boolean.class || fieldType == Boolean.class) {
                convertedValue = Boolean.parseBoolean(value.trim());
            } else {
                throw new ParseException("不支持的字段类型：" + fieldType.getName());
            }
            field.set(instance, convertedValue);
        } catch (Exception e) {
            throw new ParseException("字段" + field.getName() + "赋值失败，值：" + value, e);
        }
    }

    public static class ParseMetadataPair<HEADER, BODY> {
        private final ParseMetadata<HEADER> headerMetadata;
        private final ParseMetadata<BODY> bodyMetadata;

        public ParseMetadataPair(ParseMetadata<HEADER> headerMetadata, ParseMetadata<BODY> bodyMetadata) {
            this.headerMetadata = headerMetadata;
            this.bodyMetadata = bodyMetadata;
        }

        public ParseMetadata<HEADER> getHeaderMetadata() { return headerMetadata; }
        public ParseMetadata<BODY> getBodyMetadata() { return bodyMetadata; }
    }

    public static class ParseMetadata<T> {
        private final Charset charset;
        private final LineParser lineParser;
        private final FieldMapper<T> fieldMapper;
        private final Class<T> entityClass;

        public ParseMetadata(Charset charset, LineParser lineParser, FieldMapper<T> fieldMapper, Class<T> entityClass) {
            this.charset = charset;
            this.lineParser = lineParser;
            this.fieldMapper = fieldMapper;
            this.entityClass = entityClass;
        }

        public Charset getCharset() { return charset; }
        public LineParser getLineParser() { return lineParser; }
        public FieldMapper<T> getFieldMapper() { return fieldMapper; }
        public Class<T> getEntityClass() { return entityClass; }
    }

    @FunctionalInterface
    public interface FieldMapper<T> {
        void map(List<String> fields, T instance) throws ParseException;
    }
}