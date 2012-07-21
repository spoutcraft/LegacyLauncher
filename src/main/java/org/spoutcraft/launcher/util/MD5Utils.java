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
package org.spoutcraft.launcher.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.digest.DigestUtils;

import org.spoutcraft.launcher.api.util.FileType;
import org.spoutcraft.launcher.api.util.YAMLProcessor;
import org.spoutcraft.launcher.yml.Resources;

public class MD5Utils {
	private static final Logger logger = Logger.getLogger("launcher");
	public static String getMD5(File file) {
		try {
			FileInputStream fis = new FileInputStream(file);
			String md5 = DigestUtils.md5Hex(fis);
			fis.close();
			return md5;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getMD5(FileType type) {
		return getMD5(type, Resources.getLatestMinecraftVersion());
	}

	public static String getMD5(FileType type, String version) {
		String md5 = getMD5Internal(type, version);
		if (md5 == null) {
			logger.log(Level.WARNING, "No MD5 Information found for filetype " + type.name() + ", for minecraft version: " + version);
		}
		return md5;
	}

	@SuppressWarnings("unchecked")
	private static String getMD5Internal(FileType type, String version) {
		YAMLProcessor config = Resources.Minecraft.getYAML();
		try {
			Map<String, Object> versions = (Map<String, Object>) config.getProperty("versions");
			Map<String, Object> map = (Map<String, Object>) versions.get(version);
			return (String) map.get(type.toString());
		} catch (NullPointerException npe) {
			logger.log(Level.SEVERE, "MD5 Configuration was not populated!", npe);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to read md5", e);
		}
		return null;
	}
}
