/*
 * This file is part of Launcher (http://www.spout.org/).
 *
 * Launcher is licensed under the SpoutDev License Version 1.
 *
 * Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Launcher is distributed in the hope that it will be useful,
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

import java.io.File;
import java.util.Iterator;
import java.util.Map;

import org.jdesktop.swingworker.SwingWorker;

import org.spoutcraft.launcher.api.Launcher;
import org.spoutcraft.launcher.api.util.FileType;
import org.spoutcraft.launcher.util.MD5Utils;
import org.spoutcraft.launcher.yml.LibrariesYML;
import org.spoutcraft.launcher.yml.SpoutcraftBuild;

public class InstallValidationWorker extends SwingWorker<Object, Object> {
	private boolean passed = false;
	private SimpleGameUpdater updater;

	public InstallValidationWorker(SimpleGameUpdater updater) {
		this.updater = updater;
	}

	@Override
	protected Object doInBackground() throws Exception {
		Launcher.getGameUpdater().setStartValidationTime(System.currentTimeMillis());
		SpoutcraftBuild build = SpoutcraftBuild.getSpoutcraftBuild();
		File minecraftJar = new File(updater.getBinDir(), "minecraft.jar");
		if (minecraftJar.exists()) {
			if (!compareMD5s(FileType.MINECRAFT, minecraftJar)) {
				err("Invalid minecraft.jar");
				return null;
			}
		} else {
			err("There is no minecraft.jar!");
			return null;
		}

		File jinputJar = new File(updater.getBinDir(), "jinput.jar");
		if (jinputJar.exists()) {
			if (!compareMD5s(FileType.JINPUT, jinputJar)) {
				err("Invalid jinput.jar");
				return null;
			}
		} else {
			err("There is no jinput.jar");
			return null;
		}

		File lwjglJar = new File(updater.getBinDir(), "lwjgl.jar");
		if (lwjglJar.exists()) {
			if (!compareMD5s(FileType.LWJGL, lwjglJar)) {
				err("Invalid lwjgl.jar");
				return null;
			}
		} else {
			err("There is no lwjgl.jar");
			return null;
		}

		File lwjgl_utilJar = new File(updater.getBinDir(), "lwjgl_util.jar");
		if (lwjgl_utilJar.exists()) {
			if (!compareMD5s(FileType.LWJGL_UTIL, lwjgl_utilJar)) {
				err("Invalid lwjgl_util.jar");
				return null;
			}
		} else {
			err("There is no lwjgl_util.jar");
			return null;
		}

		File libDir = new File(updater.getBinDir(), "lib");
		Map<String, Object> libraries = build.getLibraries();
		Iterator<Map.Entry<String, Object>> i = libraries.entrySet().iterator();
		while (i.hasNext()) {
			Map.Entry<String, Object> lib = i.next();
			String version = String.valueOf(lib.getValue());
			String name = lib.getKey() + "-" + version;

			File libraryFile = new File(libDir, lib.getKey() + ".jar");

			if (libraryFile.exists()) {
				if (!compareLibraryMD5s(lib.getKey(), version, libraryFile)) {
					err("Invalid " + libraryFile.getName());
					return null;
				}
			} else {
				err("There is no " + libraryFile.getName());
				return null;
			}
		}
		passed = true;
		return null;
	}

	@Override
	protected void done() {
		updater.validationFinished(passed);
	}

	public boolean isValid() {
		return passed;
	}

	private boolean compareMD5s(FileType type, File file) {
		String expected = MD5Utils.getMD5(type);
		String actual = MD5Utils.getMD5(file);
		debug("Checking MD5 of" + type.name() + ". Expected MD5: " + expected + " | Actual MD5: " + actual);
		return expected.equals(actual);
	}

	private boolean compareLibraryMD5s(String lib, String version, File file) {
		String expected = LibrariesYML.getMD5(lib, version);
		String actual = MD5Utils.getMD5(file);
		debug("Checking MD5 of" + lib + ". Expected MD5: " + expected + " | Actual MD5: " + actual);
		return expected.equals(actual);
	}

	private void print(Object obj) {
		System.out.println(obj);
	}

	private void debug(Object obj) {
		System.out.println(obj);
	}

	private void err(Object obj) {
		System.err.println(obj);
	}
}
