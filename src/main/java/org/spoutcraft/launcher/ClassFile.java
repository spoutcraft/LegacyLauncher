/*
 * This file is part of Spoutcraft Launcher (http://www.spout.org/).
 *
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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spoutcraft.launcher;

import java.io.File;

public class ClassFile {
	private File file;
	String path;
	public ClassFile(File file, String rootDir) {
		this.file = file;
		path = file.getPath();
		path = path.replace(rootDir, "");
		path = path.replaceAll("\\\\", "/");
	}

	public ClassFile(String path) {
		this.file = null;
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public File getFile() {
		return file;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof ClassFile)) {
			return false;
		}
		return ((ClassFile)obj).path.equals(path);
	}

	public int hashCode() {
		return path.hashCode();
	}
}
