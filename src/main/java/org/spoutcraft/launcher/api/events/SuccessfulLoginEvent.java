package org.spoutcraft.launcher.api.events;

public class SuccessfulLoginEvent extends Event {

	private final String user;
	
	public SuccessfulLoginEvent(String user) {
		this.user = user;
	}

	public String getUser() {
		return user;
	}
	
}
