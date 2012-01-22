package org.spoutcraft.launcher.api.skin.exceptions;

public class SkinSecurityException extends RuntimeException {

	private static final long serialVersionUID = 1009272991066266101L;
	private final String message;
	
	public SkinSecurityException(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}
