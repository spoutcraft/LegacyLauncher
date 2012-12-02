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

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class Project{
	private final String project;
	private final int releaseChannel;
	private final int build;
	private final String version;
	private final String fileName;
	private final String fileType;
	private final String md5;
	@JsonCreator
	public Project(@JsonProperty("project") String project, @JsonProperty("release_channel") int releaseChannel, @JsonProperty("build") int build, @JsonProperty("version") String version, @JsonProperty("file_name") String fileName, @JsonProperty("file_type") String fileType, @JsonProperty("hash") String md5) {
		this.project = project;
		this.releaseChannel = releaseChannel;
		this.build = build;
		this.version = version;
		this.fileName = fileName;
		this.fileType = fileType;
		this.md5 = md5;
	}

	public String getProject() {
		return project;
	}

	public int getReleaseChannel() {
		return releaseChannel;
	}

	public int getBuild() {
		return build;
	}

	public String getVersion() {
		return version;
	}

	public String getFileName() {
		return fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public String getMd5() {
		return md5;
	}

	@Override
	public int hashCode() {
		return project.hashCode() + releaseChannel + build + version.hashCode() + fileName.hashCode() + fileType.hashCode() + md5.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Project)) {
			return false;
		}
		Project other = (Project)obj;
		return other.project.equals(project) && other.releaseChannel == releaseChannel && other.build == build && other.version.equals(version) && other.fileName.equals(fileName) && other.fileType.equals(fileType) && other.md5.equals(md5);
	}

	@Override
	public String toString() {
		return "{ Project [project: " + project + ", releaseChannel: " + releaseChannel + ", build: " + build + ", version: " + version + ", fileName: " + fileName + ", fileType: " + fileType + ", md5: " + md5 + "] }";
	}

}
