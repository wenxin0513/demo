package com.example.myexcel.core.exception;
/**
 * Ticket 加密/解密异常
 * @author harry
 *
 */
public class TicketDecodeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public TicketDecodeException(String msg) {
		super(msg);
	}

	public TicketDecodeException() {
		super();
	}

}
