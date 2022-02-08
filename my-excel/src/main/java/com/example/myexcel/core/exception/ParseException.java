package com.example.myexcel.core.exception;
/**
 * 对象转换异常
 * @author harry
 *
 */
public class ParseException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ParseException(String msg) {
		super(msg);
	}

	public ParseException() {
		super();
	}

}
