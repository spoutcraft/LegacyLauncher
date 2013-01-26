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

package org.spoutcraft.launcher.technic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;
import org.spoutcraft.launcher.exceptions.RestfulAPIException;
import org.spoutcraft.launcher.rest.Library;
import org.spoutcraft.launcher.rest.Versions;
import org.spoutcraft.launcher.util.MirrorUtils;

public class Modpack {

	@JsonProperty("libraries")
	private String libraries;
	@JsonProperty("minecraft")
	private String minecraftVersion;
	@JsonProperty("forge")
	private String forgeVersion;
	@JsonProperty("mods")
	private Map<String, String> mods;

	private String name;
	private String displayName;
	private String build;

	private ModpackInfo info;

	public Modpack setInfo(ModpackInfo info, String build) {
		this.info = info;
		this.name = info.getName();
		this.displayName = info.getDisplayName();
		this.build = build;
		return this;
	}

	public ModpackInfo getInfo() {
		return info;
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getBuild() {
		return build;
	}

	//TODO Make these two library methods the same somehow
	public List<Library> getLibraries() {
		return new ArrayList<Library>();
	}

	public String getMinecraftVersion() {
		return minecraftVersion;
	}

	public String getMinecraftURL(String user) {
		return "http://s3.amazonaws.com/MinecraftDownload/minecraft.jar?user=" + user + "&ticket=1";
	}

	public String getPatchURL() {
		String mirrorURL = "patch/minecraft_";
		mirrorURL += Versions.getLatestMinecraftVersion();
		mirrorURL += "-" + getMinecraftVersion() + ".patch";
		String fallbackURL = "http://get.spout.org/patch/minecraft_";
		fallbackURL += Versions.getLatestMinecraftVersion();
		fallbackURL += "-" + getMinecraftVersion() + ".patch";
		return MirrorUtils.getMirrorUrl(mirrorURL, fallbackURL);
	}

	public String getYMLURL() {
		return TechnicRestAPI.getModpackYMLURL(this.getName());
	}

	public String getForgeVersion() {
		return forgeVersion;
	}

	public List<Mod> getMods() {
		List<Mod> modList = new ArrayList<Mod>(mods.size());
		for (String name : mods.keySet()) {
			modList.add(new Mod(name, mods.get(name)));
		}
		return modList;
	}

	@Override
	public String toString() {
		return "{ Modpack [name: " + name + ", build: " + build + ", libraries: " + libraries + ", minecraft: " + minecraftVersion + ", forge: " + forgeVersion + ", mods: " + mods + "] }";
	}

	public String getMD5() throws RestfulAPIException {
		return TechnicRestAPI.getModpackMD5(this.getName());
	}

}
