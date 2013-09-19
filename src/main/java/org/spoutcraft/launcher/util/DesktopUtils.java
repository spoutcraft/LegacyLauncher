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

import net.technicpack.launchercore.util.Utils;
import org.spoutcraft.launcher.entrypoint.SpoutcraftLauncher;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;

/**
 * Static utility class to preventing checking for IOExceptions everywhere you would like to open a folder or open the browser
 */
public class DesktopUtils {
	/**
	 * Replaces Desktop.getDesktop().browse(uri)
	 *
	 * @param uri
	 */
	public static void browse(URI uri) {
		try {
			Desktop.getDesktop().browse(uri);
		} catch (Exception e) {
			if (SpoutcraftLauncher.params.isDebugMode()) {
				e.printStackTrace();
			}
		}
	}

	public static void open(File file) {
		try {
			Desktop.getDesktop().open(file);
		} catch (Exception e) {
			if (SpoutcraftLauncher.params.isDebugMode()) {
				e.printStackTrace();
			}
		}
	}
}
