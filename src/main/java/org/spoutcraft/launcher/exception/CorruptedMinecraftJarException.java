package org.spoutcraft.launcher.exception;

public class CorruptedMinecraftJarException extends RuntimeException{
	private final Throwable cause;
	public CorruptedMinecraftJarException(Throwable ex) {
		cause = ex;
	}
	
	public Throwable getCause() {
		return this.cause;
	}

	private static final long serialVersionUID = 5550898219922574735L;
}
