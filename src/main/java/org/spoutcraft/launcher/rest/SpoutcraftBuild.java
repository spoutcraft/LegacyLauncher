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
import org.spoutcraft.launcher.rest.exceptions.RestfulAPIException;

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
