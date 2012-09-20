package org.spoutcraft.launcher;

public enum WindowMode {
	WINDOWED("Windowed", 0),
	FULL_SCREEN("Full Screen", 1),
	MAXIMIZED("Maximized", 2);

	private final String name;
	private final int id;
	private WindowMode(final String name, final int id) {
		this.name = name;
		this.id = id;
	}

	public String getModeName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public static WindowMode getModeById(int id) {
		for (WindowMode m : values()) {
			if (m.id == id) {
				return m;
			}
		}
		throw new IllegalArgumentException("No window mode matching " + id);
	}
}
