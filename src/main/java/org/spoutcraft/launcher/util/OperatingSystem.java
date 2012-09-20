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
}
