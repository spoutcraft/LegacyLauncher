/*
 * This file is part of Spoutcraft.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * Spoutcraft is licensed under the Spout License Version 1.
 *
 * Spoutcraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spoutcraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spoutcraft.launcher.util;

import java.awt.Image;
import java.awt.Window;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URI;

import org.spoutcraft.launcher.Settings;

/**
 * Static utility class meant to allow Java 1.6 calls while maining 1.5 compability
 */
public class Compatibility {
	/**
	 * Replaces Desktop.getDesktop().browse(uri)
	 *
	 * @param uri
	 */
	public static void browse(URI uri) {
		try {
			Object o = Class.forName("java.awt.Desktop").getMethod("getDesktop", new Class[0]).invoke(null, new Object[0]);
			o.getClass().getMethod("browse", new Class[]{URI.class}).invoke(o, new Object[]{uri});
		} catch (Exception e) {
			if (Settings.isDebugMode()) {
				e.printStackTrace();
			}
		}
	}

	public static void open(File file) {
		try {
			Object o = Class.forName("java.awt.Desktop").getMethod("getDesktop", new Class[0]).invoke(null, new Object[0]);
			o.getClass().getMethod("open", new Class[]{File.class}).invoke(o, new Object[]{file});
		} catch (Exception e) {
			if (Settings.isDebugMode()) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public static void setIconImage(Window window, Image image) {
		try {
			Class[] params = {Image.class};
			Method setIconImage = Window.class.getMethod("setIconImage", params);
			setIconImage.invoke(window, image);
		} catch (Exception e) {
			if (Settings.isDebugMode()) {
				e.printStackTrace();
			}
		}
	}

	public static boolean setExecutable(File file, boolean executable, boolean owner) {
		try {
			Class[] params = {boolean.class, boolean.class};
			Method setExecutable = File.class.getMethod("setExecutable", params);
			return (Boolean)setExecutable.invoke(file, executable, owner);
		} catch (Exception e) {
			if (Settings.isDebugMode()) {
				e.printStackTrace();
			}
		}
		return false;
	}
}
