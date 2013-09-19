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

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.progress.ProgressMonitor;
import net.technicpack.launchercore.util.Utils;
import org.spoutcraft.launcher.exceptions.UnzipException;

import java.io.File;
import java.util.logging.Level;

public class ZipUtils {

	public static boolean checkLaunchDirectory(File dir) {
		if (!dir.isDirectory()) {
			return false;
		}

		if (dir.list().length == 0) {
			return true;
		}

		for (File file : dir.listFiles()) {
			if (file.getName().equals("settings.json")) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Checks if a directory is empty
	 *
	 * @param dir to check
	 * @return true if the directory is empty
	 */
	public static boolean checkEmpty(File dir) {
		if (!dir.isDirectory()) {
			return false;
		}

		return dir.list().length == 0;
	}

	/**
	 * Unzips a file into the specified directory.
	 *
	 * @param zip      file to unzip
	 * @param output   directory to unzip into
	 * @param listener to update progress on - may be null for no progress indicator
	 */
	public static void unzipFile(File zip, File output, DownloadListener listener) {
		if (!zip.exists()) {
			Utils.getLogger().log(Level.SEVERE, "File to unzip does not exist: " + zip.getAbsolutePath());
			return;
		}
		if (!output.exists()) {
			output.mkdirs();
		}
		try {
			ZipFile zipFile = new ZipFile(zip);
			zipFile.setRunInThread(true);
			zipFile.extractAll(output.getAbsolutePath());

			ProgressMonitor monitor = zipFile.getProgressMonitor();
			while (monitor.getState() == ProgressMonitor.STATE_BUSY) {
				long totalProgress = monitor.getWorkCompleted() / (monitor.getTotalWork() + 1);
				if (listener != null) {
					listener.stateChanged("Extracting " + monitor.getFileName() + "...", totalProgress);
				}
			}
		} catch (ZipException e) {
			throw new UnzipException("Error unzipping file: " + zip, e);
		}
	}
}
