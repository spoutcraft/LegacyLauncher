/*
 * This file is part of Spoutcraft.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * Spoutcraft is licensed under the Spout License Version 1.
 *
 * Spoutcraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spoutcraft is distributed in the hope that it will be useful,
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
package org.spoutcraft.launcher.rest;

import org.spoutcraft.launcher.Channel;

public class RestAPI {
	// Private
	private static final String PROJECT = "spoutcraft";

	// Public
	public static final String REST_URL = "http://get.spout.org/api/";
	public static final String VERSIONS_URL = REST_URL + "versions/" + PROJECT;
	public static final String INFO_URL = REST_URL + "info/";
	public static final String LIBRARY_GET_URL = REST_URL + "library/";
	public static final String ALL_BUILDS_URL = REST_URL + "builds/" + PROJECT;
	public static final String LAUNCHER_BUILDS_URL = REST_URL + "builds/spoutcraftlauncher";

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
}
