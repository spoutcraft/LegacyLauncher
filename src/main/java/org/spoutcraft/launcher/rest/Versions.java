package org.spoutcraft.launcher.rest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;

public final class Versions {
	private static List<String> versions = null;
	public static synchronized List<String> getMinecraftVersions() {
		if (versions == null) {
			InputStream stream = null;
			try {
				URLConnection conn = (new URL(RestAPI.VERSIONS_URL)).openConnection();
				stream = conn.getInputStream();
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
			} catch (IOException e) {
				throw new RuntimeException(e);
			} finally {
				IOUtils.closeQuietly(stream);
			}
		}
		return versions;
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


