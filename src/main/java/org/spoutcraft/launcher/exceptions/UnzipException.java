package org.spoutcraft.launcher.exceptions;

public class UnzipException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private final String message;
	private final Throwable cause;

	public UnzipException(String message, Throwable cause) {
		this.message = message;
		this.cause = cause;
	}

	public UnzipException(String message) {
		this(message, null);
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public Throwable getCause() {
		return cause;
	}
}
