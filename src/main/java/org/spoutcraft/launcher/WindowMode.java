/*
 * This file is part of Spoutcraft.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spoutcraft is licensed under the SpoutDev License Version 1.
 *
 * Spoutcraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spoutcraft is distributed in the hope that it will be useful,
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
				return new Dimension(900, 540);
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
