package org.spoutcraft.launcher.technic;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;
import org.spoutcraft.launcher.exceptions.RestfulAPIException;

public class Modpacks {

	@JsonProperty("modpacks")
	private String[] modpacks;

	public List<ModpackBuilds> getModpacks() throws RestfulAPIException {
		List<ModpackBuilds> modpackObjs = new ArrayList<ModpackBuilds>(modpacks.length);
		for (String name : modpacks) {
			modpackObjs.add(TechnicRestAPI.getModpackBuilds(name));
		}
		return modpackObjs;
	}

	@Override
	public String toString() {
		return "{ Modpacks [modpacks: " + modpacks + "] }";
	}
}
