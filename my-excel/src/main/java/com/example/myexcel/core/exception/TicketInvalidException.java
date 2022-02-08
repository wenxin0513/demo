package com.example.myexcel.core.exception;
/**
 * Ticket 不合法
 * @author harry
 *
 */
public class TicketInvalidException extends TicketDecodeException {

	private static final long serialVersionUID = 1L;

	public TicketInvalidException(String msg) {
		super(msg);
	}

	public TicketInvalidException() {
		super();
	}

}
