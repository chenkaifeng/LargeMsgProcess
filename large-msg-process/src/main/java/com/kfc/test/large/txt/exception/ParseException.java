package com.kfc.test.large.txt.exception;

/**
 * 解析过程中的异常（如格式错误、字段不匹配等）
 */
public class ParseException extends Exception {
    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }
    public ParseException(String message) {
        super(message);
    }
}