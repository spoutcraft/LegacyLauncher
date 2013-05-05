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
package org.spoutcraft.launcher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;

import org.spoutcraft.launcher.api.SpoutcraftDirectories;
import org.spoutcraft.launcher.exceptions.NoMirrorsAvailableException;
import org.spoutcraft.launcher.exceptions.RestfulAPIException;
import org.spoutcraft.launcher.rest.Library;
import org.spoutcraft.launcher.rest.MD5Result;
import org.spoutcraft.launcher.rest.Minecraft;
import org.spoutcraft.launcher.rest.Project;
import org.spoutcraft.launcher.rest.RestAPI;
import org.spoutcraft.launcher.rest.Versions;
import org.spoutcraft.launcher.util.MD5Utils;
import org.spoutcraft.launcher.util.Utils;

public final class SpoutcraftData {
	private static final ObjectMapper mapper = new ObjectMapper();
	private final String installedBuild;
	private final String build;
	private final String hash;
	private final List<Library> libs;
	private final List<Minecraft> minecraftVersions;

	/**
	 * Creates a snapshot of Spoutcraft build information
	 *
	 * @throws RestfulAPIException
	 */
	public SpoutcraftData() throws RestfulAPIException {
		minecraftVersions = requestMinecraftVersions();
		build = calculateBuild();
		installedBuild = calculateInstall();
		hash = calcaulateMD5(build);
		libs = Collections.unmodifiableList(calculateLibraries(build));
	}

	public String getMD5() {
		return hash;
	}

	public String getBuild() {
		return build;
	}

	public String getInstalledBuild() {
		return installedBuild;
	}

	public List<Library> getLibraries() {
		return libs;
	}

	public Minecraft getMinecraft() {
		String selected = Settings.getMinecraftVersion();
		if (selected.equals(Settings.DEFAULT_MINECRAFT_VERSION)) {
			// Find the latest supported SC MC version
			selected = Versions.getStableMinecraftVersions().get(0);
		}
		for (Minecraft minecraft : minecraftVersions) {
			if (selected.equalsIgnoreCase(minecraft.getVersion())) {
				return minecraft;
			}
		}
		// Should never get here...
		throw new IllegalStateException("Unknown Minecraft build: " + selected);
	}

	public Minecraft getLatestMinecraft() {
		return minecraftVersions.get(0);
	}

	public String getSpoutcraftURL() throws NoMirrorsAvailableException {
		return RestAPI.getDownloadURL(build);
	}

	/**
	 * Retrieves the md5 hashsum for the given Spoutcraft build from the REST API
	 *
	 * @param build
	 * @return md5 hashsum
	 * @throws RestfulAPIException if the REST API could not be accessed
	 */
	private static String calcaulateMD5(String build) throws RestfulAPIException {
		String url = RestAPI.getSpoutcraftURL(build);
		InputStream stream = null;
		try {
			stream = RestAPI.getCachingInputStream(new URL(url), true);
			Project project = mapper.readValue(stream, Project.class);
			return project.getMd5();
		} catch (IOException e) {
			throw new RestfulAPIException("Error accessing URL [" + url + "]", e);
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}

	/**
	 * Calculates the Spoutcraft build the launcher should retrieve information for
	 *
	 * It first checks the launcher arguments, which take first priority.
	 * It then checks the Spoutcraft custom build settings, which take second priority
	 * If neither of the above are used, it uses the build channelt to select the build
	 *
	 * @return build
	 * @throws RestfulAPIException if the REST API could not be accessed
	 */
	private String calculateBuild() throws RestfulAPIException {
		int buildArg = Utils.getStartupParameters().getSpoutcraftBuild();
		if (buildArg > 0) {
			return String.valueOf(buildArg);
		}
		Channel channel = Settings.getSpoutcraftChannel();
		if (channel == Channel.CUSTOM) {
			return Settings.getSpoutcraftSelectedBuild();
		}
		InputStream stream = null;
		// Use channel selection for latest
		if (getLatestMinecraft().equals(getMinecraft())) {
			int build;
			String url = RestAPI.getSpoutcraftURL(channel);
			try {
				stream = RestAPI.getCachingInputStream(new URL(url), true);
				Project project = mapper.readValue(stream, Project.class);
				build = project.getBuild();
			} catch (IOException e) {
				throw new RestfulAPIException("Error accessing URL [" + url + "]", e);
			} finally {
				IOUtils.closeQuietly(stream);
			}
			// Check to see if stable has newer release
			if (channel == Channel.STABLE) {
				return String.valueOf(build);
			} else {
				url = RestAPI.getSpoutcraftURL(Channel.STABLE);
				try {
					stream = RestAPI.getCachingInputStream(new URL(url), true);
					Project stable = mapper.readValue(stream, Project.class);
					// Stable release is newer
					if (stable.getBuild() > build) {
						return String.valueOf(stable.getBuild());
					} else {
						return String.valueOf(build);
					}
				} catch (IOException e) {
					throw new RestfulAPIException("Error accessing URL [" + url + "]", e);
				} finally {
					IOUtils.closeQuietly(stream);
				}
			}
		} else {
			// Find the newest build for the Minecraft version
			String url = RestAPI.ALL_BUILDS_URL;
			try {
				final String mcVersion = getMinecraft().getVersion();
				stream = RestAPI.getCachingInputStream(new URL(url), true);
				ChannelData data = mapper.readValue(stream, ChannelData.class);
				HashSet<String> builds = new HashSet<String>(100);
				for (VersionData v : data.stable) {
					if (v.minecraftVersion.equals(mcVersion)) {
						builds.add(v.buildNumber);
					}
				}
				for (VersionData v : data.beta) {
					if (v.minecraftVersion.equals(mcVersion)) {
						builds.add(v.buildNumber);
					}
				}
				for (VersionData v : data.dev) {
					if (v.minecraftVersion.equals(mcVersion)) {
						builds.add(v.buildNumber);
					}
				}
				ArrayList<String> buildList = new ArrayList<String>(builds);
				Collections.sort(buildList);
				return buildList.get(buildList.size() - 1);
			} catch (IOException e) {
				throw new RestfulAPIException("Error accessing URL [" + url + "]", e);
			} finally {
				IOUtils.closeQuietly(stream);
			}
		}
	}

	/**
	 * Calculates the Spoutcraft build the launcher has currently installed
	 *
	 * If the Spoutcraft jar exists, it creates a md5 hashsum of the jar and retrieves
	 * information from the build, if information exists
	 *
	 * @return build, or -1 if could not retrieve information
	 * @throws RestfulAPIException if the REST API could not be accessed
	 */
	private static String calculateInstall() throws RestfulAPIException {
		File spoutcraft = new File((new SpoutcraftDirectories()).getBinDir(), "spoutcraft.jar");
		if (spoutcraft.exists()) {
			String md5 = MD5Utils.getMD5(spoutcraft);
			InputStream stream = null;
			String url = RestAPI.getMD5URL(md5);
			try {
				stream = RestAPI.getCachingInputStream(new URL(url), true);
				MD5Result result = mapper.readValue(stream, MD5Result.class);
				return result.getBuildNumber();
			} catch (IOException e) {
				// Ignore
			} finally {
				IOUtils.closeQuietly(stream);
			}
		}
		return "-1";
	}

	/**
	 * Retrieves the libraries associated with the given build from the REST API
	 *
	 * @param build
	 * @return list of libraries
	 * @throws RestfulAPIException if the REST API could not be accessed
	 */
	private static List<Library> calculateLibraries(String build) throws RestfulAPIException {
		InputStream stream = null;
		String url = RestAPI.getLibraryURL(build);
		try {
			stream = RestAPI.getCachingInputStream(new URL(url), true);
			List<String> json = IOUtils.readLines(stream);
			String fullJson = "";
			for (String j : json) {
				fullJson += j;
			}
			List<Library> libs;
			try {
				libs = new ArrayList<Library>(Arrays.asList(mapper.readValue(fullJson, LibraryWrapper.class).spoutcraft));
			} catch (IOException e) {
				try {
					libs = new ArrayList<Library>(Arrays.asList(mapper.readValue(fullJson, Library[].class)));
				} catch (IOException e2) {
					throw e;
				}
			}
			Iterator<Library> i = libs.iterator();
			// Handled separately
			while (i.hasNext()) {
				Library lib = i.next();
				if (lib.name().contains("lwjgl")) {
					i.remove();
				}
			}
			return libs;
		} catch (IOException e) {
			throw new RestfulAPIException("Error accessing URL [" + url + "]", e);
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}

	private static class LibraryWrapper {
		@JsonProperty("spoutcraft")
		Library[] spoutcraft;
		@JsonProperty("minecraft")
		Library[] minecraft;
	}

	private static class ChannelData {
		@JsonProperty("stable")
		VersionData[] stable;
		@JsonProperty("beta")
		VersionData[] beta;
		@JsonProperty("dev")
		VersionData[] dev;
	}

	private static class VersionData {
		@JsonProperty("build_number")
		String buildNumber;
		@JsonProperty("build_version")
		String minecraftVersion;
	}
	
	private List<Minecraft> requestMinecraftVersions() throws RestfulAPIException {
		InputStream stream = null;
		try {
			stream = RestAPI.getCachingInputStream(new URL(RestAPI.MINECRAFT_URL), true);
			Minecraft[] versions = mapper.readValue(stream, Minecraft[].class);
			List<Minecraft> list = new ArrayList<Minecraft>(Arrays.asList(versions));
			Collections.sort(list);
			return Collections.unmodifiableList(list);
		} catch (IOException e) {
			throw new RestfulAPIException("Error accessing URL [" + RestAPI.MINECRAFT_URL + "]", e);
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}

}
