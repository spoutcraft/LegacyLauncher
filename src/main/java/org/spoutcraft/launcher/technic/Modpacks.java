package org.spoutcraft.launcher.technic;

import org.codehaus.jackson.annotate.JsonProperty;
import org.spoutcraft.launcher.exceptions.RestfulAPIException;

public class Modpacks {

	@JsonProperty("modpacks")
	private String[] modpacks;

	public ModpackInfo[] getModpacks() throws RestfulAPIException {
		ModpackInfo[] modpackInfos = new ModpackInfo[modpacks.length];
		for (int i = 0; i < modpacks.length; i++) {
			modpackInfos[i] = TechnicRestAPI.getModpackInfo(modpacks[i]);
		}
		return modpackInfos;
	}

	@Override
	public String toString() {
		return "{ Modpacks [modpacks: " + modpacks + "] }";
	}
}
