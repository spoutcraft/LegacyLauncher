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
