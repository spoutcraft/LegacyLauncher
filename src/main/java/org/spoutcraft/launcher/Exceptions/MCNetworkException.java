package org.spoutcraft.launcher.Exceptions;

public class MCNetworkException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5521671148991031931L;
	private final Throwable cause;
	private final String message;
	
	public MCNetworkException(String message) {
	  this(null, message);
	}

	public MCNetworkException(Throwable throwable, String message) {
	  this.cause = null;
	  this.message = message;
	}

	public MCNetworkException() {
	  this(null, "Could not connect to minecraft.net");
	}

	public Throwable getCause() {
	  return this.cause;
	}

	public String getMessage() {
	  return this.message;
	}
}
