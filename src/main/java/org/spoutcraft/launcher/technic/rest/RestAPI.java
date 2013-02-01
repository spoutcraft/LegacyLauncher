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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.spoutcraft.launcher.api.Launcher;
import org.spoutcraft.launcher.exceptions.RestfulAPIException;
import org.spoutcraft.launcher.rest.Versions;
import org.spoutcraft.launcher.technic.rest.info.CustomInfo;
import org.spoutcraft.launcher.technic.rest.info.RestInfo;
import org.spoutcraft.launcher.technic.rest.pack.RestModpack;
import org.spoutcraft.launcher.util.MirrorUtils;

public class RestAPI {

	private final static ObjectMapper mapper = new ObjectMapper();
	public static final RestAPI TECHNIC = new RestAPI("http://www.sctgaming.com/Technic/API/");

	private final String restURL;
	private final String restInfoURL;
	private final String cacheURL;
	private final String modURL;
	private final String mirrorURL;

	private final Modpacks modpacks;

	public RestAPI(String url) {
		restURL = url;
		restInfoURL = restURL + "modpack/";
		cacheURL = restURL + "cache/";
		modURL = cacheURL + "mod/";
		modpacks = getModpacks();
		modpacks.setRest(this);
		mirrorURL = modpacks.getMirrorURL();
	}

	public String getModDownloadURL(String mod, String build) {
		return getMirrorURL() + "mods/" + mod + "/" + mod + "-" + build + ".zip";
	}

	public String getModMD5URL(String mod, String build) {
		return modURL + mod + "/" + build + "/MD5";
	}

	public String getModpackURL(String modpack, String build) {
		return restInfoURL + modpack + "/build/" + build;
	}

	public String getModpackMD5URL(String modpack) {
		return restInfoURL + modpack + "/MD5";
	}

	public String getModpackInfoURL(String modpack) {
		return restInfoURL + modpack;
	}

	public String getModpackImgURL(String modpack) {
		return getMirrorURL() + modpack + "/resources/logo_180.png";
	}

	public String getModpackBackgroundURL(String modpack) {
		return getMirrorURL() + modpack + "/resources/background.jpg";
	}

	public String getModpackIconURL(String modpack) {
		return getMirrorURL() + modpack + "/resources/icon.png";
	}

	private Modpacks getModpacks() {
		InputStream stream = null;
		String url = restInfoURL;
		try {
			URL conn = new URL(url);
			stream = conn.openConnection().getInputStream();
			Modpacks result = mapper.readValue(stream, Modpacks.class);
			return result;
		} catch (IOException e) {
			Launcher.getFrame().handleException(new RestfulAPIException("Error accessing URL [" + url + "]", e));
			return null;
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}

	public String getMirrorURL() {
		return mirrorURL;
	}

	public List<RestInfo> getRestInfos() throws RestfulAPIException {
		return modpacks.getModpacks();
	}

	public String getModMD5(String mod, String build) throws RestfulAPIException {
		InputStream stream = null;
		String url = getModMD5URL(mod, build);
		try {
			URL conn = new URL(url);
			stream = conn.openConnection().getInputStream();
			TechnicMD5 md5Result = mapper.readValue(stream, TechnicMD5.class);
			return md5Result.getMD5();
		} catch (IOException e) {
			throw new RestfulAPIException("Error accessing URL [" + url + "]", e);
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}

	public RestModpack getModpack(RestInfo modpack, String build) throws RestfulAPIException {
		InputStream stream = null;
		String url = getModpackURL(modpack.getName(), build);
		try {
			URL conn = new URL(url);
			stream = conn.openConnection().getInputStream();
			RestModpack result = mapper.readValue(stream, RestModpack.class);
			result.setRest(this);
			return result.setInfo(modpack, build);
		} catch (IOException e) {
			throw new RestfulAPIException("Error accessing URL [" + url + "]", e);
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}

	public RestInfo getModpackInfo(String modpack) throws RestfulAPIException {
		InputStream stream = null;
		String url = getModpackInfoURL(modpack);
		try {
			URL conn = new URL(url);
			stream = conn.openStream();
			RestInfo result = mapper.readValue(stream, RestInfo.class);
			result.setRest(this);
			String display = modpacks.getDisplayName(modpack);
			if (display != null) {
				result.setDisplayName(display);
			}
			return result;
		} catch (IOException e) {
			throw new RestfulAPIException("Error accessing URL [" + url + "]", e);
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}

	public static CustomInfo getCustomModpack(String packURL) throws RestfulAPIException {
		InputStream stream = null;
		String url = packURL;
		try {
			URL conn = new URL(url);
			stream = conn.openStream();
			CustomInfo result = mapper.readValue(stream, CustomInfo.class);
			return result;
		} catch (IOException e) {
			throw new RestfulAPIException("Error accessing URL [" + url + "]", e);
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}

	public String getLatestBuild(String modpack) throws RestfulAPIException {
		return getModpackInfo(modpack).getLatest();
	}

	public String getRecommendedBuild(String modpack) throws RestfulAPIException {
		return getModpackInfo(modpack).getRecommended();
	}

	public String getModpackMD5(String modpack) throws RestfulAPIException {
		InputStream stream = null;
		String url = getModpackMD5URL(modpack);
		try {
			URL conn = new URL(url);
			stream = conn.openConnection().getInputStream();
			TechnicMD5 md5Result = mapper.readValue(stream, TechnicMD5.class);
			return md5Result.getMD5();
		} catch (IOException e) {
			throw new RestfulAPIException("Error accessing URL [" + url + "]", e);
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}
	
	public static int getLatestLauncherBuild() throws RestfulAPIException {
		InputStream stream = null;
		String url = "http://beta.technicpack.net/api/launcher/version/latest";
		try {
			URL conn = new URL(url);
			stream = conn.openConnection().getInputStream();
			LauncherBuild buildResult = mapper.readValue(stream, LauncherBuild.class);
			return buildResult.getLatestBuild();
		} catch (IOException e) {
			throw new RestfulAPIException("Error accessing URL [" + url + "]", e);
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}
	
	public static String getLauncherDownloadURL(int version, Boolean isJar) throws RestfulAPIException {
		String ext = null;
		if (isJar) {
			ext = "jar";
		} else {
			ext = "exe";
		}
		
		InputStream stream = null;
		String url = "http://beta.technicpack.net/api/launcher/url/" + version + "/" + ext;
		try {
			URL conn = new URL(url);
			stream = conn.openConnection().getInputStream();
			LauncherURL buildURL = mapper.readValue(stream, LauncherURL.class);
			return buildURL.getLauncherURL();
		} catch (IOException e) {
			throw new RestfulAPIException("Error accessing URL [" + url + "]", e);
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}

	public static String getMinecraftURL(String user) {
		return "http://s3.amazonaws.com/MinecraftDownload/minecraft.jar?user=" + user + "&ticket=1";
	}

	public static String getPatchURL(Modpack build) {
		String mirrorURL = "patch/minecraft_";
		mirrorURL += Versions.getLatestMinecraftVersion();
		mirrorURL += "-" + build.getMinecraftVersion() + ".patch";
		String fallbackURL = "http://get.spout.org/patch/minecraft_";
		fallbackURL += Versions.getLatestMinecraftVersion();
		fallbackURL += "-" + build.getMinecraftVersion() + ".patch";
		return MirrorUtils.getMirrorUrl(mirrorURL, fallbackURL);
	}

	private class TechnicMD5 {
		@JsonProperty("MD5")
		String md5;

		public String getMD5() {
			return md5;
		}
	}
	
	private class LauncherBuild {
		@JsonProperty("LatestBuild")
		int latestBuild;
		
		public int getLatestBuild() {
			return latestBuild;
		}
	}
	
	private class LauncherURL {
		@JsonProperty("URL")
		String launcherURL;
		
		public String getLauncherURL() {
			return launcherURL;
		}
	}
}
