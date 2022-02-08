package com.example.myexcel.core.exception;

/**
 * @author zhouhong
 * @version 1.0
 * @title: JcoException
 * @description: JCO操作异常
 * @date 2019/8/9 17:30
 */
public class JcoException extends RuntimeException {

    public JcoException(Throwable cause) {
        super(cause);
    }

    public JcoException(String message) {
        super(message);
    }
}
