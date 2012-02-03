package org.spoutcraft.launcher.api;

import java.io.File;
import java.io.IOException;

import org.spoutcraft.launcher.api.util.DownloadListener;
import org.spoutcraft.launcher.api.util.Utils;

public abstract class GameUpdater extends SpoutcraftDirectories implements DownloadListener {

	/* Minecraft Updating Arguments */
	private String user = "Player";
	private String downloadTicket = "1";
	private String minecraftPass = "";
	private String minecraftSession = "";

	public final String baseURL = "http://s3.amazonaws.com/MinecraftDownload/";
	public final String latestLWJGLURL = "http://get.spout.org/Libraries/lwjgl/";
	public final String spoutcraftMirrors = "http://cdn.getspout.org/mirrors.html";

	private DownloadListener listener;

	public void setMinecraftUser(String user) {
		this.user = user;
	}

	public void setDownloadTicket(String downloadTicket) {
		this.downloadTicket = downloadTicket;
	}

	public String getMinecraftUser() {
		return user;
	}

	public String getDownloadTicket() {
		return downloadTicket;
	}

	public String getMinecraftPass() {
		return minecraftPass;
	}

	public String getMinecraftSession() {
		return minecraftSession;
	}
	
	public void setMinecraftPass(String minecraftPass) {
		this.minecraftPass = minecraftPass;
	}
	
	public void setMinecraftSession(String minecraftSession) {
		this.minecraftSession = minecraftSession;
	}

	public DownloadListener getDownloadListener() {
		return listener;
	}

	public void setDownloadListener(DownloadListener listener) {
		this.listener = listener;
	}

	public void stateChanged(String message, float progress) {
		if (listener != null)
			listener.stateChanged(message, progress);
	}

	public abstract boolean isSpoutcraftUpdateAvailible();

	public abstract boolean isMinecraftUpdateAvailible();

	public abstract void updateMinecraft() throws IOException;

	public abstract void updateSpoutcraft() throws Exception;

	public abstract void runValidator();
	
	public void runGame() {
		Launcher.getGameLauncher().runGame(user,  minecraftSession, downloadTicket, minecraftPass);

	}
}
