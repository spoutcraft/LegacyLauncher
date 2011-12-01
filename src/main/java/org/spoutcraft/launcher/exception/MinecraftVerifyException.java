package org.spoutcraft.launcher.exception;

public class MinecraftVerifyException extends Exception{
	private static final long serialVersionUID = 1L;
	private final Throwable cause;
	private final String message;
	
	public MinecraftVerifyException(String message) {
		this(null, message);
	}

	public MinecraftVerifyException(Throwable throwable, String message) {
		this.cause = throwable;
		this.message = message;
	}
	
	public MinecraftVerifyException(Throwable throwable) {
		this.cause = throwable;
		this.message = null;
	}

	public Throwable getCause() {
		return this.cause;
	}

	public String getMessage() {
		return this.message;
	}
}