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

package org.spoutcraft.launcher;

import org.spoutcraft.launcher.api.Build;
import org.spoutcraft.launcher.api.Launcher;
import org.spoutcraft.launcher.api.util.YAMLProcessor;

public class Settings {

	private static YAMLProcessor settings;

	public static synchronized void setSettings(YAMLProcessor settings) {
		if (Settings.settings != null)
			throw new IllegalArgumentException("settings is already set!");
		Settings.settings = settings;
		try {
			Settings.settings.load();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static synchronized YAMLProcessor getSettings() {
		return settings;
	}

	public static synchronized int getLauncherSelectedBuild() {
		return settings.getInt("launcher.launcher.buildNumber", -1);
	}

	public static synchronized void setLauncherSelectedBuild(int build) {
		settings.setProperty("launcher.launcher.buildNumber", build);
	}

	public static synchronized Build getLauncherBuild() {
		return Build.getValue(settings.getString("launcher.launcher.build", "RECOMMENDED"));
	}

	private static synchronized void setLauncherBuild(String build) {
		settings.setProperty("launcher.launcher.build", build);
	}

	public static synchronized void setLauncherBuild(Build build) {
		setLauncherBuild(build.name());
	}

	public static synchronized int getSpoutcraftSelectedBuild() {
		return settings.getInt("launcher.client.buildNumber", -1);
	}

	public static synchronized void setSpoutcraftSelectedBuild(int build) {
		settings.setProperty("launcher.client.buildNumber", build);
	}

	public static synchronized Build getSpoutcraftBuild() {
		return Build.getValue(settings.getString("launcher.client.build", "RECOMMENDED"));
	}

	private static synchronized void setSpoutcraftBuild(String build) {
		settings.setProperty("launcher.client.build", build);
	}

	public static synchronized void setSpoutcraftBuild(Build build) {
		setSpoutcraftBuild(build.name());
		Launcher.getGameUpdater().onSpoutcraftBuildChange();
	}

	public static synchronized int getLoginTries() {
		return settings.getInt("launcher.loginRetries", 3);
	}

	public static synchronized void setLoginTries(int tries) {
		settings.setProperty("launcher.loginRetries", tries);
	}

	public static synchronized boolean allowClipboardAccess() {
		return settings.getBoolean("client.clipboardaccess", false);
	}

	public static synchronized void setClipboardAccess(boolean allow) {
		settings.setProperty("client.clipboardaccess", allow);
	}

	public static synchronized boolean isAcceptUpdates() {
		return settings.getBoolean("launcher.acceptUpdates", false);
	}

	public static synchronized void setAcceptUpdates(boolean acceptUpdates) {
		settings.setProperty("launcher.acceptUpdates", acceptUpdates);
	}

	public static synchronized int getMemory() {
		return settings.getInt("launcher.memory", 0);
	}

	public static synchronized void setMemory(int memory) {
		settings.setProperty("launcher.memory", 0);
	}

	public static synchronized boolean retryLogin() {
		return settings.getBoolean("launcher.retryLogin", true);
	}

	public static synchronized void setRetryLogin(boolean retry) {
		settings.setProperty("launcher.retryLogin", retry);
	}

}
