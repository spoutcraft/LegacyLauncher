package org.spoutcraft.launcher;

import org.spoutcraft.launcher.async.DownloadListener;

public class SpoutcraftBuild {
	private String minecraftVersion;
	private String latestVersion;
	private int build;
	private DownloadListener listener = null;
	public SpoutcraftBuild(String minecraft, String latest, int build) {
		this.minecraftVersion = minecraft;
		this.latestVersion = latest;
		this.build = build;
	}
	
	public int getBuild() {
		return build;
	}
	
	public String getMinecraftVersion() {
		return minecraftVersion;
	}
	
	public String getLatestMinecraftVersion() {
		return latestVersion;
	}
	
	public String getMinecraftURL(String user) {
		return "http://s3.amazonaws.com/MinecraftDownload/minecraft.jar?user=" + user + "&ticket=1";
	}
	
	public String getSpoutcraftURL() {
		return MirrorUtils.getMirrorUrl("Spoutcraft/" + build + "/spoutcraft-dev-SNAPSHOT.jar", null, listener);
	}
	
	public void setDownloadListener(DownloadListener listener) {
		this.listener = listener;
	}
	
	public String getPatchURL() {
		String mirrorURL = "/Patches/Minecraft/minecraft_";
		mirrorURL += getLatestMinecraftVersion();
		mirrorURL += "-" + getMinecraftVersion() + ".patch";
		String fallbackURL = "http://mirror3.getspout.org/Patches/Minecraft/minecraft_";
		fallbackURL += getLatestMinecraftVersion();
		fallbackURL += "-" + getMinecraftVersion() + ".patch";
		return MirrorUtils.getMirrorUrl(mirrorURL, fallbackURL, null);
	}
}
