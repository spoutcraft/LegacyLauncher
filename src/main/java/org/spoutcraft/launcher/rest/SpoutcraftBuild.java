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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.spoutcraft.launcher.Channel;
import org.spoutcraft.launcher.exceptions.RestfulAPIException;

public class SpoutcraftBuild implements Comparable<SpoutcraftBuild>{
	private final String buildNumber;
	private final String minecraftVersion;

	@JsonCreator
	public SpoutcraftBuild(@JsonProperty("build_number") String buildNumber, @JsonProperty("build_version") String minecraftVersion) {
		this.buildNumber = buildNumber;
		this.minecraftVersion = minecraftVersion;
	}

	public String getBuildNumber() {
		return buildNumber;
	}

	public String getMinecraftVersion() {
		return minecraftVersion;
	}

	@Override
	public int hashCode() {
		return buildNumber.hashCode() + minecraftVersion.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SpoutcraftBuild)) {
			return false;
		}
		SpoutcraftBuild other = (SpoutcraftBuild)obj;
		return other.buildNumber.equals(buildNumber) && other.minecraftVersion.equals(minecraftVersion);
	}

	@Override
	public String toString() {
		return "Build: "  + buildNumber + " (MC: " + minecraftVersion + ")";
	}

	public int compareTo(SpoutcraftBuild o) {
		return o.buildNumber.compareTo(buildNumber);
	}

	public static synchronized List<SpoutcraftBuild> getBuildList() throws RestfulAPIException{
		InputStream stream = null;
		HashSet<SpoutcraftBuild> uniqueBuilds = new HashSet<SpoutcraftBuild>();
		for (Channel c : Channel.values()) {
			if (c != Channel.CUSTOM) {
				try {
					URLConnection conn = (new URL(RestAPI.getBuildListURL(c))).openConnection();
					stream = conn.getInputStream();
					ObjectMapper mapper = new ObjectMapper();
					uniqueBuilds.addAll(Arrays.asList(mapper.readValue(stream, SpoutcraftBuild[].class)));
				} catch (IOException e) {
					throw new RestfulAPIException("Error reading spoutcraft build list", e);
				} finally {
					IOUtils.closeQuietly(stream);
				}
			}
		}
		ArrayList<SpoutcraftBuild> buildList = new ArrayList<SpoutcraftBuild>(uniqueBuilds);
		Collections.sort(buildList);
		return buildList;
	}
}
