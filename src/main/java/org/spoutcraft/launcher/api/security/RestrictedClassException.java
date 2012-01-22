package org.spoutcraft.launcher.api.security;

public class RestrictedClassException extends RuntimeException {

	private static final long serialVersionUID = 358132160001399981L;
	private final String message;

	public RestrictedClassException(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return super.toString();
	}

}
