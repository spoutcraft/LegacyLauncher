/*
 * This file is part of LauncherAPI (http://www.spout.org/).
 *
 * LauncherAPI is licensed under the SpoutDev License Version 1.
 *
 * LauncherAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * LauncherAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */

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

	public abstract void setStartValidationTime(long validationTime);

	public abstract void clearVersionsInYMLs();

	public void runGame() {
		Launcher.getGameLauncher().runGame(user, minecraftSession, downloadTicket, minecraftPass);

	}
}
