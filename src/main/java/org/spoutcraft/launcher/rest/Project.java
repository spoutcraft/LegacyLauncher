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
	public Project(@JsonProperty("project") String project, @JsonProperty("release_channel") int releaseChannel, @JsonProperty("build") int build, @JsonProperty("version") String version, @JsonProperty("fileName") String fileName, @JsonProperty("file_type") String fileType, @JsonProperty("hash") String md5) {
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
		return "{ Library [project: " + project + ", releaseChannel: " + releaseChannel + ", build: " + build + ", version: " + version + ", fileName: " + fileName + ", fileType: " + fileType + ", md5: " + md5 + "] }";
	}

}
