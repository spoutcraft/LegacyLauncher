package org.spoutcraft.launcher.technic.rest;

import java.util.Collection;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;
import org.spoutcraft.launcher.technic.RestInfo;

public class FullModpacks extends RestObject {
	@JsonProperty("modpacks")
	private Map<String, RestInfo> modpacks;
	@JsonProperty("mirror_url")
	private String mirrorURL;

	public Collection<RestInfo> getModpacks() {
		return modpacks.values();
	}

	public String getMirrorURL() {
		return mirrorURL;
	}

	@Override
	public String toString() {
		return "{ Full Modpacks [modpacks: " + modpacks + "] }";
	}

	public Map<String, RestInfo> getMap() {
		return modpacks;
	}
}
