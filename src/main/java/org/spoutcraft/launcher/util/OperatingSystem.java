/*
 * This file is part of Technic Launcher.
 * Copyright (C) 2013 Syndicate, LLC
 *
 * Technic Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Technic Launcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Technic Launcher.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.spoutcraft.launcher.util;

public enum OperatingSystem {
	UNIX("Unix"),
	LINUX("Linux"),
	SOLARIS("Solaris"),
	WINDOWS_XP("Windows XP"),
	WINDOWS_VISTA("Windows Vista"),
	WINDOWS_7("Windows 7"),
	WINDOWS_8("Windows 8"),
	WINDOWS_UNKNOWN("Windows"),
	MAC_OSX("Mac OS X"),
	MAC("Mac"),
	UNKNOWN("");

	private final String identifier;
	OperatingSystem(String system) {
		this.identifier = system.toLowerCase();
	}

	public boolean isUnix() {
		return this == UNIX || this == LINUX || this == SOLARIS;
	}

	public boolean isMac() {
		return this == MAC_OSX || this == MAC;
	}

	public boolean isWindows() {
		return this == WINDOWS_XP ||  this == WINDOWS_VISTA ||  this == WINDOWS_7 ||  this == WINDOWS_8 ||  this == WINDOWS_UNKNOWN;
	}

	public static OperatingSystem getOS() {
		OperatingSystem best = UNKNOWN;
		final String os = System.getProperty("os.name").toLowerCase();
		for (OperatingSystem system : values()) {
			if (os.contains(system.identifier)) {
				if (system.identifier.length() > best.identifier.length()) {
					best = system;
				}
			}
		}
		return best;
	}

	public static String getNativeValue() {
		OperatingSystem OS = getOS();
		if (OS.isWindows()) {
			return "win";
		}

		if (OS.isMac()) {
			return "osx";
		}

		if (OS.isUnix()) {
			return "nix";
		}
		return null;
	}
}
