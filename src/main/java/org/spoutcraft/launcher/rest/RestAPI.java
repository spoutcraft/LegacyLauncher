/*
 * This file is part of Spoutcraft Launcher.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spoutcraft Launcher is licensed under the Spout License Version 1.
 *
 * Spoutcraft Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spoutcraft Launcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license,
 * including the MIT license.
 */
package org.spoutcraft.launcher.rest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.spout.downpour.DownpourCache;
import org.spout.downpour.NoCacheException;
import org.spout.downpour.connector.DefaultURLConnector;
import org.spout.downpour.connector.DownloadURLConnector;

import org.spoutcraft.launcher.Channel;
import org.spoutcraft.launcher.entrypoint.SpoutcraftLauncher;
import org.spoutcraft.launcher.util.Utils;

public class RestAPI {
	// Private
	private static final String PROJECT = "spoutcraft";
	private static DownpourCache cache = new DownpourCache(new File(new File(Utils.getWorkingDirectory(), "cache"), ".rest-cache"));

	// Public
	public static final String REST_URL = "http://get.spout.org/api/";
	public static final String VERSIONS_URL = REST_URL + "versions/" + PROJECT;
	public static final String INFO_URL = REST_URL + "info/";
	public static final String LIBRARY_GET_URL = REST_URL + "library/";
	public static final String ALL_BUILDS_URL = REST_URL + "builds/" + PROJECT;
	public static final String LAUNCHER_BUILDS_URL = REST_URL + "builds/spoutcraftlauncher";
	public static final String MINECRAFT_URL = REST_URL + "minecraft";
	static {
		try {
			int response = SpoutcraftLauncher.pingURL(REST_URL);
			cache.setOfflineMode(response / 100 != 2); 
		} catch (Exception e) {
			e.printStackTrace();
			cache.setOfflineMode(true);
		}
	}

	public static String getSpoutcraftURL(Channel channel) {
		if (channel != Channel.CUSTOM) {
			return INFO_URL + channel.toString() + "/" + PROJECT;
		}
		throw new IllegalArgumentException("No download URL available for custom channel builds");
	}

	public static String getLauncherURL(Channel channel) {
		if (channel != Channel.CUSTOM) {
			return INFO_URL + channel.toString() + "/" + "spoutcraftlauncher";
		}
		throw new IllegalArgumentException("No download URL available for custom channel builds");
	}

	public static String getLauncherDownloadURL(Channel channel, boolean jar) {
		if (channel != Channel.CUSTOM) {
			return REST_URL + channel.toString() + "/" + "spoutcraftlauncher" + (jar ? ".jar" : ".exe");
		}
		throw new IllegalArgumentException("No download URL available for custom channel builds");
	}

	public static String getSpoutcraftURL(String build) {
		return INFO_URL + "build/" + build + "/" + PROJECT;
	}

	public static String getDownloadURL(String build) {
		return REST_URL + "build/" + build + "/" + PROJECT + ".jar";
	}

	public static String getLibraryURL(String build) {
		return REST_URL + "libraries/build/" + build;
	}

	public static String getBuildListURL(Channel channel) {
		if (channel != Channel.CUSTOM) {
			return REST_URL + "builds/" + channel.toString() + "/" + PROJECT;
		}
		throw new IllegalArgumentException("No download URL available for custom channel builds");
	}

	public static String getMD5URL(String md5) {
		return REST_URL + "hash/" + md5;
	}

	public static InputStream getCachingInputStream(URL url, boolean force) throws NoCacheException, IOException {
		return cache.get(url, force ? new DownloadURLConnector() : new DefaultURLConnector(), force);
	}

	public static DownpourCache getCache() {
		return cache;
	}
}
