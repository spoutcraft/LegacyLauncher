package org.spoutcraft.launcher;

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.awt.Window;

import org.spoutcraft.launcher.util.OperatingSystem;

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
	
	public Dimension getDimension(Window window) {
		DisplayMode mode;
		switch(this) {
			case WINDOWED: 
				if (OperatingSystem.getOS() == OperatingSystem.WINDOWS_8) {
					return new Dimension(900, 540);
				} else {
					return new Dimension(880, 520);
				}
			case FULL_SCREEN:
			case MAXIMIZED:
				mode = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
				return new Dimension(mode.getWidth(), mode.getHeight());
			default:
				throw new IllegalArgumentException("Unknown windowmode");
		}
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
