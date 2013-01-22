package org.spoutcraft.launcher.technic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;
import org.spoutcraft.launcher.exceptions.RestfulAPIException;

public class Modpacks {

	@JsonProperty("modpacks")
	private Map<String, String> modpacks;

	public List<ModpackInfo> getModpacks() throws RestfulAPIException {
		List<ModpackInfo> modpackInfos = new ArrayList<ModpackInfo>(modpacks.size());
		for (String pack : modpacks.keySet()) {
			ModpackInfo info = TechnicRestAPI.getModpackInfo(pack);
			info.setDisplayName(modpacks.get(pack));
			modpackInfos.add(info);
		}
		return modpackInfos;
	}

	@Override
	public String toString() {
		return "{ Modpacks [modpacks: " + modpacks + "] }";
	}
}
