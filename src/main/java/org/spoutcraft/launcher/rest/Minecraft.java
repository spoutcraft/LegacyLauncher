package org.spoutcraft.launcher.rest;

import java.util.Arrays;
import java.util.List;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public final class Minecraft implements Comparable<Minecraft>{
	private final String version;
	private final String md5;
	private final Library[] libraries;
	@JsonCreator
	public Minecraft(@JsonProperty("minecraft_version") String version, @JsonProperty("minecraft_hash") String md5, @JsonProperty("libraries") Library[] libraries) {
		this.version = version;
		this.md5 = md5;
		this.libraries = libraries;
	}

	public String getVersion() {
		return version;
	}

	public String getMd5() {
		return md5;
	}

	public List<Library> getLibraries() {
		return Arrays.asList(libraries);
	}

	public int compareTo(Minecraft other) {
		String[] versions = version.split("\\.");
		String[] otherVersions = other.version.split("\\.");

		int majorVersion = Integer.parseInt(versions[0]);
		int otherMajorVersion = Integer.parseInt(otherVersions[0]);

		int compare = compareVersions(otherMajorVersion, majorVersion);
		if (compare != 0) {
			return compare;
		}

		if (versions.length > 1 && otherVersions.length > 1) {
			int minorVersion = Integer.parseInt(versions[1]);
			int otherMinorVersion = Integer.parseInt(otherVersions[1]);
	
			compare = compareVersions(otherMinorVersion, minorVersion);
			if (compare != 0) {
				return compare;
			}

			if (versions.length > 2 && otherVersions.length > 2) {
				int buildVersion = Integer.parseInt(versions[2]);
				int otherBuildVersion = Integer.parseInt(otherVersions[2]);
				
				compare = compareVersions(otherBuildVersion, buildVersion);
				if (compare != 0) {
					return compare;
				}
				return 0;
			}
		}
		compare = compareVersions(otherVersions.length, versions.length);
		return compare;
	}

	private int compareVersions(int v1, int v2) {
		if (v1 > v2) {
			return 1;
		} else if (v1 < v2) {
			return -1;
		}
		return 0;
	}
}
