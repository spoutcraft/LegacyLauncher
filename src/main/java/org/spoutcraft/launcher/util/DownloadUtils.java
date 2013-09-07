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

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.spoutcraft.launcher.exceptions.DownloadException;
import org.spoutcraft.launcher.util.Download.Result;

public class DownloadUtils {
	private static final int DOWNLOAD_RETRIES = 3;
	public static Download downloadFile(String url, String output, String cacheName, String md5, DownloadListener listener) throws IOException {
		int tries = DOWNLOAD_RETRIES;
		File outputFile = null;
		Download download = null;
		while (tries > 0) {
			System.out.println("Starting download of " + url + ", with " + tries + " tries remaining");
			tries--;
			download = new Download(url, output);
			download.setListener(listener);
			download.run();
			if (download.getResult() != Result.SUCCESS) {
				if (download.getOutFile() != null) {
					download.getOutFile().delete();
				}
				System.err.println("Download of " + url + " Failed!");
				if (listener != null) {
					listener.stateChanged("Download failed, retries remaining: " + tries, 0F);
				}
			} else {
				if (md5 != null) {
					String resultMD5 = MD5Utils.getMD5(download.getOutFile());
					System.out.println("Expected MD5: " + md5 + " Calculated MD5: " + resultMD5);
					if (md5.equalsIgnoreCase(resultMD5)) {
						outputFile = download.getOutFile();
						break;
					}
				} else {
					outputFile = download.getOutFile();
					break;
				}
			}
		}
		if (outputFile == null) {
			throw new DownloadException("Failed to download " + url, download != null ? download.getException() : null);
		}
		if (cacheName != null) {
			File cacheFolder = Utils.getCacheDirectory();
			cacheFolder.mkdirs();
			File cacheFile = new File(cacheFolder, cacheName);
			org.apache.commons.io.FileUtils.copyFile(outputFile, cacheFile);
		}
		return download;
	}

	public static Download downloadFile(String url, String output, String cacheName) throws IOException {
		return downloadFile(url, output, cacheName, null, null);
	}

	public static Download downloadFile(String url, String output) throws IOException {
		return downloadFile(url, output, null);
	}
}
