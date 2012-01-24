package org.spoutcraft.launcher.api.events;

public class MinecraftNetworkDownEvent extends Event {
	private final boolean offlineMode;
	
	public MinecraftNetworkDownEvent(boolean offlineMode) {
		this.offlineMode = offlineMode;
	}
	
	public boolean canPlayOffline() {
		return offlineMode;
	}
}
