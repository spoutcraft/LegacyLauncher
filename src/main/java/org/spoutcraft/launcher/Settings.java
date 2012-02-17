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
import org.spoutcraft.launcher.api.util.YAMLProcessor;

public class Settings {

	private static YAMLProcessor settings;
	
	public static void setSettings(YAMLProcessor settings) {
		if (Settings.settings != null) 
			throw new IllegalArgumentException("settings is already set!");
		Settings.settings = settings;
		try {
		    Settings.settings.load();
		} catch (Exception e) {
		    e.printStackTrace();
		}
		
	}

	public static YAMLProcessor getSettings() {
		return settings;
	}

	public static int getLauncherSelectedBuild() {
		return settings.getInt("launcher.launcher.buildNumber", 5);
	}
	
	public static void setLauncherSelectedBuild(int build) {
		settings.setProperty("launcher.launcher.buildNumber", build);
	}
	
	public static Build getLauncherBuild() {
		return Build.getValue(settings.getString("launcher.launcher.build", "RECOMMENED"));
	}
	
	public static void setLauncherBuild(String build) {
		settings.setProperty("launcher.launcher.build", build);
	}
	
	public static void setLauncherBuild(Build build) {
		setLauncherBuild(build.name());
	}

	public static int getSpoutcraftSelectedBuild() {
		return settings.getInt("launcher.client.buildNumber", 5);
	}

	public static void setSpoutcraftSelectedBuild(int build) {
		settings.setProperty("launcher.client.buildNumber", build);
	}

	public static Build getSpoutcraftBuild() {
		return Build.getValue(settings.getString("launcher.client.build", "RECOMMENED"));
	}

	public static void setSpoutcraftBuild(String build) {
		settings.setProperty("launcher.client.build", build);
	}
	
	public static void setSpoutcraftBuild(Build build) {
		setSpoutcraftBuild(build.name());
	}

	public static int getLoginTries() {
		return settings.getInt("launcher.loginRetries", 3);
	}
	
	public static void setLoginTries(int tries) {
		settings.setProperty("launcher.loginRetries", tries);
	}

	public static boolean isLatestLWJGL() {
		return settings.getBoolean("launcher.latestLWJGL", false);
	}
	
	public static void setLatestLWJGL(boolean latestLWJGL) {
		settings.setProperty("launcher.latestLWJGL", latestLWJGL);
	}

	public static boolean allowClipboardAccess() {
		return settings.getBoolean("client.clipboardaccess", false);
	}
	
	public static void setClipboardAccess(boolean allow) {
		settings.setProperty("client.clipboardaccess", allow);
	}
	
	public static boolean isAcceptUpdates() {
		return settings.getBoolean("launcher.acceptUpdates", false);
	}
	
	public static void setAcceptUpdates(boolean acceptUpdates) {
		settings.setProperty("launcher.acceptUpdates", acceptUpdates);
	}
	
	public static int getMemory() {
		return settings.getInt("launcher.memory", 0);
	}
	
	public static void setMemory(int memory) {
		settings.setProperty("launcher.memory", 0);
	}
	
	public static boolean retryLogin() {
		return settings.getBoolean("launcher.retryLogin", true);
	}

	public static void setRetryLogin(boolean retry) {
		settings.setProperty("launcher.retryLogin", retry);
	}

}
