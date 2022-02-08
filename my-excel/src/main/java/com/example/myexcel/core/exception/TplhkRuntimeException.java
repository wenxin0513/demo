package com.example.myexcel.core.exception;

/**
 * @author zhouhong
 * @date 2018/11/28
 */
public class TplhkRuntimeException extends RuntimeException {

    public TplhkRuntimeException(String message){
        super(message);
    }

    public TplhkRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
