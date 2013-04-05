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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.spoutcraft.launcher.VersionComparator;

public final class Versions {
	private static List<String> versions = null;
	public static synchronized List<String> getMinecraftVersions() {
		if (versions == null) {
			InputStream stream = null;
			try {
				stream = RestAPI.getCachingInputStream(new URL(RestAPI.VERSIONS_URL), true);
				ObjectMapper mapper = new ObjectMapper();
				Channel c = mapper.readValue(stream, Channel.class);

				Set<String> versions = new HashSet<String>();
				for (Version version : c.releaseChannel.stable) {
					versions.add(version.version);
				}
				for (Version version : c.releaseChannel.beta) {
					versions.add(version.version);
				}
				for (Version version : c.releaseChannel.dev) {
					versions.add(version.version);
				}
				Versions.versions = new ArrayList<String>(versions);
				Collections.sort(Versions.versions, new VersionComparator());
			} catch (IOException e) {
				throw new RuntimeException(e);
			} finally {
				IOUtils.closeQuietly(stream);
			}
		}
		return new ArrayList<String>(versions);
	}

	public static synchronized List<String> getStableMinecraftVersions() {
		if (versions == null) {
			InputStream stream = null;
			try {
				stream = RestAPI.getCachingInputStream(new URL(RestAPI.VERSIONS_URL), true);
				ObjectMapper mapper = new ObjectMapper();
				Channel c = mapper.readValue(stream, Channel.class);

				Set<String> versions = new HashSet<String>();
				for (Version version : c.releaseChannel.stable) {
					versions.add(version.version);
				}
				Versions.versions = new ArrayList<String>(versions);
				Collections.sort(Versions.versions, new VersionComparator());
			} catch (IOException e) {
				throw new RuntimeException(e);
			} finally {
				IOUtils.closeQuietly(stream);
			}
		}
		return new ArrayList<String>(versions);
	}

	private static class Channel {
		@JsonProperty("release_channel")
		private ChannelType releaseChannel;
	}

	private static class ChannelType {
		@JsonProperty("dev")
		private Version[] dev;
		@JsonProperty("beta")
		private Version[] beta;
		@JsonProperty("stable")
		private Version[] stable;
	}

	private static class Version {
		@JsonProperty("version")
		private String version;
	}
}
