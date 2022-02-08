package com.example.myexcel.core.exception;
/**
 * Ticket 过期
 * @author harry
 *
 */
public class TicketExpireException extends TicketDecodeException {

	private static final long serialVersionUID = 1L;

	public TicketExpireException(String msg) {
		super(msg);
	}

	public TicketExpireException() {
		super();
	}

}
