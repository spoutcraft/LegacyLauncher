/*
 * This file is part of Spoutcraft Launcher (http://www.spout.org/).
 *
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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spoutcraft.launcher;

import java.io.File;

public class SettingsUtil {
	private static File settingsFolder = new File(PlatformUtils.getWorkingDirectory(), "spoutcraft");
	private static File settingsFile = new File(settingsFolder, "spoutcraft.properties"));
	private static SettingsHandler settings = new SettingsHandler("defaults/spoutcraft.properties", settingsFile);

	static {
		if (!settingsFolder.exists()) {
			settingsFolder.mkdir();
		}
		if (!settingsFile.exists()) {
			settingsFile.createNewFile();
		}
		settings.load();
	}

	public static boolean isFastLogin() {
		return isProperty("fastLogin");
	}

	public static void setFastLogin(boolean fast) {
		setProperty("fastLogin", fast);
	}

	public static boolean isAcceptUpdates() {
		return isProperty("acceptUpdates");
	}

	public static void setAcceptUpdates(boolean accept) {
		setProperty("acceptUpdates", accept);
	}

	public static boolean isLatestLWJGL() {
		return isProperty("latestLWJGL");
	}

	public static void setLatestLWJGL(boolean value) {
		setProperty("latestLWJGL", value);
	}

	public static boolean isWorldBackup() {
		return isProperty("worldbackup");
	}

	public static void setWorldBackup(boolean value) {
		setProperty("worldbackup", value);
	}

	public static int getLoginTries() {
		return isProperty("retryLogins", true) ? 3 : 1;
	}

	public static void setLoginTries(boolean value){
		setProperty("retryLogins", value);
	}

	public static boolean isRecommendedBuild() {
		return isProperty("recupdate", true);
	}

	public static void setRecommendedBuild(boolean value) {
		setProperty("recupdate", value);
	}

	public static int getSelectedBuild() {
		return getProperty("custombuild", -1);
	}

	public static void setSelectedBuild(int value) {
		setProperty("custombuild", value);
	}

	public static boolean isDevelopmentBuild() {
		return isProperty("devupdate");
	}

	public static void setDevelopmentBuild(boolean value) {
		setProperty("devupdate", value);
	}

	public static boolean isClipboardAccess() {
		return isProperty("clipboardaccess");
	}

	public static void setClipboardAccess(boolean value) {
		setProperty("clipboardaccess", value);
	}

	public static int getMemorySelection() {
		return getProperty("memory", 1);
	}

	public static void setMemorySelection(int value) {
		setProperty("memory", value);
	}

	private static void setProperty(String s, Object value) {
		if (settings.checkProperty(s)) {
			settings.changeProperty(s, value);
		} else {
			settings.put(s, value);
		}
	}

	private static boolean isProperty(String s) {
		return isProperty(s, false);
	}

	private static boolean isProperty(String s, boolean def) {
		if (settings.checkProperty(s)) {
			return settings.getPropertyBoolean(s);
		}
		settings.put(s, def);
		return def;
	}

	private static int getProperty(String s, int def) {
		if (settings.checkProperty(s)) {
			return settings.getPropertyInteger(s);
		}
		settings.put(s, def);
		return def;
	}
}
