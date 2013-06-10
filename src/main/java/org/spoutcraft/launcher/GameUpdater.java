/*
 * This file is part of Technic Launcher.
 *
 * Copyright (c) 2013-2013, Technic <http://www.technicpack.net/>
 * Technic Launcher is licensed under the Spout License Version 1.
 *
 * Technic Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Technic Launcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spoutcraft.launcher;

import org.spoutcraft.launcher.api.Launcher;
import org.spoutcraft.launcher.exceptions.RestfulAPIException;
import org.spoutcraft.launcher.launch.MinecraftLauncher;
import org.spoutcraft.launcher.technic.PackInfo;

public final class GameUpdater {
	// Minecraft Updating Arguments
	private String user = "Player";
	private String downloadTicket = "1";
	private String minecraftSession = "";

	private UpdateThread updateThread;

	public GameUpdater() {
	}

	public void start(PackInfo installedPack) throws RestfulAPIException {
		if (updateThread != null) {
			return;
		}
		this.updateThread = new UpdateThread(installedPack);
		MinecraftLauncher.resetClassLoader();
		System.setProperty("minecraft.applet.TargetDirectory", installedPack.getPackDirectory().getAbsolutePath());
		updateThread.start();
	}

	public void resetUpdateThread() {
		Launcher.getGameLauncher().setShouldRun(false);
		updateThread = null;
	}

	public boolean isFinished() {
		return updateThread.isFinished();
	}

	public void setWaiting(boolean waiting) {
		updateThread.setWaiting(waiting);
	}

	public void setMinecraftUser(String user) {
		this.user = user;
	}

	public void setDownloadTicket(String downloadTicket) {
		this.downloadTicket = downloadTicket;
	}

	public void setMinecraftSession(String minecraftSession) {
		this.minecraftSession = minecraftSession;
	}

	public void runGame(PackInfo pack) {
		Launcher.getGameLauncher().runGame(user, minecraftSession, downloadTicket, pack);
	}
}
