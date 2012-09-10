package org.spoutcraft.launcher;

public enum LauncherBuild {
	STABLE(0),
	BETA(1),
	DEV(2);

	private final int type;

	private LauncherBuild(int type) {
		this.type = type;
	}

	public int type() {
		return type;
	}

	public static LauncherBuild getType(int type) {
		for (LauncherBuild b : values()) {
			if (b.type == type) {
				return b;
			}
		}
		throw new IllegalArgumentException("Unknown launcher build type: " + type);
	}
}
