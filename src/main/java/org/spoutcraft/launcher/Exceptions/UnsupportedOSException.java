package org.spoutcraft.launcher.Exceptions;

public class UnsupportedOSException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 521197180311147444L;
	private final Throwable cause;
	private final String message;
	
	public UnsupportedOSException(String message) {
	  this(null, message);
	}

	public UnsupportedOSException(Throwable throwable, String message) {
	  this.cause = null;
	  this.message = message;
	}

	public UnsupportedOSException() {
	  this(null, "Bad login");
	}

	public Throwable getCause() {
	  return this.cause;
	}

	public String getMessage() {
	  return this.message;
	}
}
