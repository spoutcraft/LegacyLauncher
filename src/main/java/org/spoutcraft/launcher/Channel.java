package org.spoutcraft.launcher;

public enum Channel {
	STABLE(0),
	BETA(1),
	DEV(2),
	CUSTOM(3);

	private final int type;

	private Channel(int type) {
		this.type = type;
	}

	public int type() {
		return type;
	}

	public static Channel getType(int type) {
		for (Channel b : values()) {
			if (b.type == type) {
				return b;
			}
		}
		throw new IllegalArgumentException("Unknown launcher build type: " + type);
	}

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
