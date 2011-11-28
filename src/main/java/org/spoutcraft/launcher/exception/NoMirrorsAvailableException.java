package org.spoutcraft.launcher.exception;

import java.io.IOException;

public class NoMirrorsAvailableException extends IOException{
	private static final long serialVersionUID = 1L;
	private final Throwable cause;
	private final String message;
	
	public NoMirrorsAvailableException(String message) {
	  this(null, message);
	}

	public NoMirrorsAvailableException(Throwable throwable, String message) {
	  this.cause = null;
	  this.message = message;
	}

	public NoMirrorsAvailableException() {
	  this(null, "No Mirrors Are Available");
	}

	public Throwable getCause() {
	  return this.cause;
	}

	public String getMessage() {
	  return this.message;
	}
}
