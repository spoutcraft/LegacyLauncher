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
import java.util.ArrayList;
import java.util.Collections;

public class BackupCleanupThread extends Thread {
	File[] oldFiles;
	public BackupCleanupThread(File[] oldFiles) {
		this.oldFiles = oldFiles;
	}

	public void run() {
		ArrayList<Integer> builds = new ArrayList<Integer>();
		for (File file : oldFiles) {
			if (file.getPath().endsWith("-backup.zip")) {
				try {
					String path = file.getPath();
					String split[] = path.split("\\\\");
					path = split[split.length - 1];
					int build = Integer.parseInt(path.split("-")[0]);
					builds.add(build);
				} catch (Exception e) { }
			}
		}

		if (builds.size() < 6) {
			return;
		}

		Collections.sort(builds);

		int minSafeBuild = builds.get(builds.size() - 5);
		for (File file : oldFiles) {
			if (file.getPath().endsWith("-backup.zip")) {
				try {
					String path = file.getPath();
					String split[] = path.split("\\\\");
					path = split[split.length - 1];
					int build = Integer.parseInt(path.split("-")[0]);
					if (build < minSafeBuild) {
						file.delete();
					}
				} catch (Exception e) { }
			} else if (file.getPath().endsWith(".tmp")) {
				file.delete();
			}
		}
	}
}
