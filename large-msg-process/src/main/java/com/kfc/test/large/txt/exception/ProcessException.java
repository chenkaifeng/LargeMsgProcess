package com.kfc.test.large.txt.exception;

/**
 * 数据处理过程中的异常（如业务校验失败）
 */
public class ProcessException extends Exception {
    public ProcessException(String message, Throwable cause) {
        super(message, cause);
    }
    public ProcessException(String message) {
        super(message);
    }
}
