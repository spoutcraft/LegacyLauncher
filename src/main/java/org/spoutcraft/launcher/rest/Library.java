package org.spoutcraft.launcher.rest;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

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

	public String getUrl(String prefix) {
		StringBuilder builder = new StringBuilder(prefix);
		return builder.append(groupId).append("/").append(artifactId).append("/").append(version).toString();
	}

	public boolean valid(String m5d) {
		return md5.equalsIgnoreCase(m5d);
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
