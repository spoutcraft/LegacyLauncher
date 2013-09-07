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

package org.spoutcraft.launcher;

import org.spoutcraft.launcher.skin.TechnicLoginFrame;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public class Launcher {
	private static Launcher instance;
	private final Logger logger = Logger.getLogger("org.spoutcraft.launcher.Main");
	private final TechnicLoginFrame loginFrame;

	public Launcher(final TechnicLoginFrame frame) {
		if (Launcher.instance != null) {
			throw new IllegalArgumentException("You can't have a duplicate launcher");
		}

		this.loginFrame = frame;
		logger.addHandler(new ConsoleHandler());
		instance = this;
	}

	public static Logger getLogger() {
		return instance.logger;
	}

	public static TechnicLoginFrame getFrame() {
		return instance.loginFrame;
	}
}
