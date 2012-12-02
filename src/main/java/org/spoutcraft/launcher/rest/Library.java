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

import java.io.File;
import java.io.IOException;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.spoutcraft.launcher.exceptions.DownloadException;
import org.spoutcraft.launcher.util.DownloadListener;
import org.spoutcraft.launcher.util.DownloadUtils;

public final class Library implements Downloadable{
	private final String groupId;
	private final String artifactId;
	private final String version;
	private final String md5;
	@JsonCreator
	public Library(@JsonProperty("groupId") String groupId, @JsonProperty("artifactId") String artifactId, @JsonProperty("version") String version, @JsonProperty("hash") String md5) {
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
		this.md5 = md5;
	}

	public void download(File location, DownloadListener listener) throws DownloadException{
		StringBuilder builder = new StringBuilder(RestAPI.LIBRARY_GET_URL);
		String url =  builder.append(groupId).append("/").append(artifactId).append("/").append(version).toString();
		try {
			DownloadUtils.downloadFile(url, location.getPath(), location.getName(), md5, listener);
		} catch (IOException e) {
			throw new DownloadException(e);
		}
	}

	public boolean valid(String m5d) {
		return md5.equalsIgnoreCase(m5d);
	}

	public String name() {
		return artifactId + "-" + version;
	}

	@Override
	public int hashCode() {
		return groupId.hashCode() + artifactId.hashCode() + version.hashCode() + md5.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Library)) {
			return false;
		}
		Library other = (Library)obj;
		return other.artifactId.equals(artifactId) && other.groupId.equals(groupId) && other.version.equals(version) && other.md5.equals(md5);
	}

	@Override
	public String toString() {
		return "{ Library [artifactId: " + artifactId + ", groupdId: " + groupId + ", version: " + version + ", md5: " + md5 + "] }";
	}
}
