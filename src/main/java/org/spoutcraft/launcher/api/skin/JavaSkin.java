/*
 * This file is part of Spoutcraft Launcher.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spoutcraft Launcher is licensed under the SpoutDev License Version 1.
 *
 * Spoutcraft Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spoutcraft Launcher is distributed in the hope that it will be useful,
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
package org.spoutcraft.launcher.api.skin;

import java.io.File;

public abstract class JavaSkin implements Skin {
	private File file;
	private File dataFolder;
	private boolean enabled;

	public final File getDataFolder() {
		return dataFolder;
	}

	public final void initialize(File dataFolder, File file) {
		this.dataFolder = dataFolder;
		this.file = file;
	}

	public final void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public final boolean isEnabled() {
		return enabled;
	}

	public final File getFile() {
		return file;
	}
}
