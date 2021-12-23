package com.example.myexcel.exception;

/**
 * @author zhouhong
 * @version 1.0
 * @title: ExcelReadConvertException
 * @date 2020/1/9 15:15
 */
public class ExcelReadConvertException extends RuntimeException {

    public ExcelReadConvertException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExcelReadConvertException(String message) {
        super(message);
    }

    public ExcelReadConvertException(Throwable cause) {
        super(cause);
    }
}
