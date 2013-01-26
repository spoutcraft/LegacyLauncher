/*
 * This file is part of Technic Launcher.
 *
 * Copyright (c) 2013-2013, Technic <http://www.technicpack.net/>
 * Technic Launcher is licensed under the Spout License Version 1.
 *
 * Technic Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Technic Launcher is distributed in the hope that it will be useful,
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

package org.spoutcraft.launcher.api;

import java.io.File;

import org.spoutcraft.launcher.util.Utils;

public class Directories {
	private File workingDir;
	private File backupDir;
	private File binDir;
	private File cacheDir;
	private File configDir;
	private File savesDir;
	private File tempDir;
	private File resourceDir;

	public void setWorkingDir(String directory) {
		workingDir = new File(Utils.getLauncherDirectory(), directory);

		backupDir = new File(workingDir, "backups");
		binDir = new File(workingDir, "bin");
		cacheDir = new File(binDir, "cache");
		configDir = new File(workingDir, "config");
		savesDir = new File(workingDir, "saves");
		tempDir = new File(workingDir, "temp");
		resourceDir = new File(workingDir, "resources");

		backupDir.mkdirs();
		binDir.mkdirs();
		cacheDir.mkdirs();
		configDir.mkdirs();
		savesDir.mkdirs();
		tempDir.mkdirs();
		resourceDir.mkdirs();

		System.setProperty("minecraft.applet.TargetDirectory", workingDir.getAbsolutePath());
	}

	public final File getWorkingDir() {
		return workingDir;
	}

	public final File getBinDir() {
		return binDir;
	}

	public final File getCacheDir() {
		return cacheDir;
	}

	public final File getBackupDir() {
		return backupDir;
	}

	public final File getConfigDir() {
		return configDir;
	}

	public final File getSavesDir() {
		return savesDir;
	}

	public final File getTempDir() {
		return tempDir;
	}

	public final File getResourceDir() {
		return resourceDir;
	}
}
