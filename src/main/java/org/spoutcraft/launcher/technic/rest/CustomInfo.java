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
package org.spoutcraft.launcher.technic.rest;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.codehaus.jackson.annotate.JsonProperty;
import org.spoutcraft.launcher.util.Download;
import org.spoutcraft.launcher.util.DownloadUtils;
import org.spoutcraft.launcher.util.Utils;

public class CustomInfo {
	@JsonProperty("name")
	private String displayName;

	@JsonProperty("user")
	private String user;

	@JsonProperty("friendly_name")
	private String name;

	@JsonProperty("version")
	private String version;

	@JsonProperty("url")
	private String url;

	@JsonProperty("logo")
	private String logoUrl;

	@JsonProperty("background")
	private String backgroundUrl;

	@JsonProperty("mirror")
	private boolean hasMirror;
	
	@JsonProperty("mirror_url")
	private String mirrorUrl;

	@JsonProperty("minecraft")
	private String minecraftVersion;

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getVersion() {
		return version;
	}

	public String getMinecraftVersion() {
		return minecraftVersion;
	}

	public String getUser() {
		return user;
	}

	public String getURL() {
		return url;
	}

	public String getLogoURL() {
		return logoUrl;
	}

	public String getBackgroundURL() {
		return backgroundUrl;
	}

	public boolean hasMirror() {
		return hasMirror;
	}

	public String getMirrorURL() {
		return mirrorUrl;
	}

	public BufferedImage getLogo() throws IOException {
		BufferedImage image;
		File temp = new File(Utils.getAssetsDirectory(), getName() + File.separator + "logo.png");
		if (temp.exists()) {
			image = ImageIO.read(temp);
		} else {
			temp.mkdirs();
			Download download = DownloadUtils.downloadFile(getLogoURL(), temp.getAbsolutePath());
			image = ImageIO.read(download.getOutFile());
		}
		return image;
	}

	public BufferedImage getBackground() throws IOException {
		BufferedImage image;
		File temp = new File(Utils.getAssetsDirectory(), getName() + File.separator + "background.jpg");
		if (temp.exists()) {
			image = ImageIO.read(temp);
		} else {
			temp.mkdirs();
			Download download = DownloadUtils.downloadFile(getBackgroundURL(), temp.getAbsolutePath());
			image = ImageIO.read(download.getOutFile());
		}
		return image;
	}
}
