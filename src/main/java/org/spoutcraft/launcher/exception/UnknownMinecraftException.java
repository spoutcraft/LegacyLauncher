package org.spoutcraft.launcher.exception;

public class UnknownMinecraftException extends RuntimeException{
	private final Throwable cause;
	public UnknownMinecraftException(Throwable ex) {
		cause = ex;
	}
	
	public Throwable getCause() {
		return this.cause;
	}

	private static final long serialVersionUID = 1L;
}

