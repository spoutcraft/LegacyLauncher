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
package org.spoutcraft.launcher.api;

import java.net.MalformedURLException;

import org.spoutcraft.launcher.api.skin.exceptions.SkinSecurityException;
import org.spoutcraft.launcher.api.util.Download;
import org.spoutcraft.launcher.api.util.Utils;

public class DownloadManager {
	public final double key;
	public final String[] safeExtensions = {"txt", "yml", "xml", "properties", "png", "jpg", "gif", "bmp"};

	public DownloadManager(final double key) {
		this.key = key;
	}

	public Download getDownload(String url, String out) throws MalformedURLException {
		String ext = Utils.getFileExtention(out);
		if (!isSafe(ext)) {
			throw new SkinSecurityException(new StringBuilder().append("The skin '").append(Launcher.getSkinManager().getEnabledSkin().getDescription().getName()).append("' tried to download a file that could be harmful.").toString());
		}

		return new Download(url, out);
	}

	public Download getUnrestrictedDownload(String url, String out, double key) throws MalformedURLException {
		if (key != this.key)
			throw new SkinSecurityException(new StringBuilder().append("The skin '").append(Launcher.getSkinManager().getEnabledSkin().getDescription().getName()).append("' used the wrong key to unlock the DownloadManager.").toString());
		return new Download(url, out);
	}

	public boolean isSafe(String ext) {
		for (String safe : safeExtensions) {
			if (ext.equalsIgnoreCase(safe)) {
				return true;
			}
		}
		return false;
	}
}
