package org.spoutcraft.launcher.technic;

import java.util.Arrays;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class ModpackBuilds {
	@JsonProperty("name")
	private String name;
	@JsonProperty("recommended")
	private String recommended;
	@JsonProperty("latest")
	private String latest;
	@JsonProperty("builds")
	private String[] builds;

	public String getRecommended() {
		return recommended;
	}

	public String getLatest() {
		return latest;
	}

	public List<String> getBuilds() {
		return Arrays.asList(builds);
	}

	@Override
	public String toString() {
		return "{ ModpackBuilds [name: " + name + ", recommended: " + recommended + ", latest: " + latest + ", builds: " + builds + "] }";
	}
}
