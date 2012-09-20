package org.spoutcraft.launcher.rest.exceptions;

import java.io.IOException;

public class RestfulAPIException extends IOException{
	private final Throwable cause;
	private final String message;

	public RestfulAPIException(String message, Throwable cause) {
		this.cause = cause;
		this.message = message;
	}

	public RestfulAPIException(Throwable cause) {
		this(null, cause);
	}

	public RestfulAPIException(String message) {
		this(message, null);
	}

	public RestfulAPIException() {
		this(null, null);
	}

	public Throwable getCause() {
		return this.cause;
	}

	public String getMessage() {
		return message;
	}

	private static final long serialVersionUID = 1L;
}
