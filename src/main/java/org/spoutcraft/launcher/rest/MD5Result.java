package org.spoutcraft.launcher.rest;

import org.codehaus.jackson.annotate.JsonProperty;

public class MD5Result {
	@JsonProperty("file_date")
	private String fileDate;
	@JsonProperty("build_number")
	private String buildNumber;
	@JsonProperty("release_channel")
	private String releaseChannel;
	@JsonProperty("file_version")
	private String fileVersion;
	@JsonProperty("file_name")
	private String fileName;
	@JsonProperty("file_type")
	private String fileType;
	@JsonProperty("file_size")
	private String fileSize;
	@JsonProperty("file_downloads")
	private String fileDownloads;

	public String getFileDate() {
		return fileDate;
	}

	public String getBuildNumber() {
		return buildNumber;
	}

	public String getReleaseChannel() {
		return releaseChannel;
	}

	public String getFileVersion() {
		return fileVersion;
	}

	public String getFileName() {
		return fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public String getFileSize() {
		return fileSize;
	}

	public String getFileDownloads() {
		return fileDownloads;
	}
}
