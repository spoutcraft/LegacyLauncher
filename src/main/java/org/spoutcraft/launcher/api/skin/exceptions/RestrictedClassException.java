package org.spoutcraft.launcher.api.skin.exceptions;

public class RestrictedClassException extends ClassNotFoundException {
	
	private static final long serialVersionUID = -5160321924997334617L;
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
