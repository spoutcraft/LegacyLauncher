/*
 * This file is part of Spoutcraft Launcher.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spoutcraft Launcher is licensed under the SpoutDev License Version 1.
 *
 * Spoutcraft Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spoutcraft Launcher is distributed in the hope that it will be useful,
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
import org.spoutcraft.launcher.api.util.YAMLProcessor;

public class Settings {
	private static YAMLProcessor yaml;

	public static synchronized void setYAML(YAMLProcessor settings) {
		if (Settings.yaml != null) {
			throw new IllegalArgumentException("settings is already set!");
		}
		Settings.yaml = settings;
		try {
			Settings.yaml.load();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static synchronized YAMLProcessor getYAML() {
		return yaml;
	}

	public static synchronized int getLauncherBuild() {
		return yaml.getInt("launcher.launcher.buildNumber", -1);
	}

	public static synchronized void setLauncherBuild(int build) {
		yaml.setProperty("launcher.launcher.buildNumber", build);
	}

	public static synchronized LauncherBuild getLauncherChannel() {
		return LauncherBuild.getType(yaml.getInt("launcher.launcher.type", 0));
	}

	public static synchronized void setLauncherChannel(LauncherBuild build) {
		yaml.setProperty("launcher.launcher.type", build.type());
	}

	public static synchronized boolean isDebugMode() {
		return yaml.getInt("launcher.launcher.debug", 0) == 1;
	}

	public static synchronized void setDebugMode(boolean b) {
		yaml.setProperty("launcher.launcher.debug", b ? 1 : 0);
	}

	public static synchronized int getSpoutcraftSelectedBuild() {
		return yaml.getInt("launcher.client.buildNumber", -1);
	}

	public static synchronized void setSpoutcraftSelectedBuild(int build) {
		yaml.setProperty("launcher.client.buildNumber", build);
	}

	public static synchronized Build getSpoutcraftBuild() {
		return Build.getValue(yaml.getString("launcher.client.build", "RECOMMENDED"));
	}

	private static synchronized void setSpoutcraftBuild(String build) {
		yaml.setProperty("launcher.client.build", build);
	}

	public static synchronized void setSpoutcraftBuild(Build build) {
		setSpoutcraftBuild(build.name());
	}

	public static synchronized int getLoginTries() {
		return yaml.getInt("launcher.loginRetries", 3);
	}

	public static synchronized void setLoginTries(int tries) {
		yaml.setProperty("launcher.loginRetries", tries);
	}

	public static synchronized boolean allowClipboardAccess() {
		return yaml.getBoolean("client.clipboardaccess", false);
	}

	public static synchronized void setClipboardAccess(boolean allow) {
		yaml.setProperty("client.clipboardaccess", allow);
	}

	public static synchronized boolean isAcceptUpdates() {
		return yaml.getBoolean("launcher.acceptUpdates", false);
	}

	public static synchronized void setAcceptUpdates(boolean acceptUpdates) {
		yaml.setProperty("launcher.acceptUpdates", acceptUpdates);
	}

	public static synchronized int getMemory() {
		return yaml.getInt("launcher.memory", 0);
	}

	public static synchronized void setMemory(int memory) {
		yaml.setProperty("launcher.memory", memory);
	}

	public static synchronized boolean retryLogin() {
		return yaml.getBoolean("launcher.retryLogin", true);
	}

	public static synchronized void setRetryLogin(boolean retry) {
		yaml.setProperty("launcher.retryLogin", retry);
	}
}
