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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.spoutcraft.launcher.exceptions.RestfulAPIException;
import org.spoutcraft.launcher.technic.RestInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Modpacks extends RestObject {

	@JsonProperty("modpacks")
	private Map<String, String> modpacks;
	@JsonProperty("mirror_url")
	private String mirrorURL;

	public String getDisplayName(String modpack) {
		return modpacks.get(modpack);
	}

	public List<RestInfo> getModpacks() throws RestfulAPIException {
		List<RestInfo> modpackInfos = new ArrayList<RestInfo>(modpacks.size());
		for (String pack : modpacks.keySet()) {
			RestInfo info = getRest().getModpackInfo(pack);
			modpackInfos.add(info);
		}
		return modpackInfos;
	}

	public String getMirrorURL() {
		return mirrorURL;
	}

	@Override
	public String toString() {
		return "{ Modpacks [modpacks: " + modpacks + "] }";
	}

	public Map<String, String> getMap() {
		return modpacks;
	}
}
