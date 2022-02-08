package com.example.myexcel.core.exception;

import lombok.NoArgsConstructor;

/**
 * @author zhangxinxin
 * @date 2018年06月22日16:22:03
 * 403 授权拒绝
 */
@NoArgsConstructor
public class TplhkDeniedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public TplhkDeniedException(String message) {
		super(message);
	}

	public TplhkDeniedException(Throwable cause) {
		super(cause);
	}

	public TplhkDeniedException(String message, Throwable cause) {
		super(message, cause);
	}

	public TplhkDeniedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
