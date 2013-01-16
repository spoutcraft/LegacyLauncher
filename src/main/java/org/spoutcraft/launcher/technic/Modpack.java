package org.spoutcraft.launcher.technic;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;

public class Modpack {

	@JsonProperty("libraries")
	private String[] libraries;
	@JsonProperty("minecraft")
	private String minecraftVersion;
	@JsonProperty("forge")
	private String forgeVersion;
	@JsonProperty("mods")
	private Mod[] mods;

	public List<String> getModLibraries() {
		return Arrays.asList(libraries);
	}

	public String getMinecraftVersion() {
		return minecraftVersion;
	}

	public String getForgeVersion() {
		return forgeVersion;
	}

	public Map<String, String> getMods() {
		HashMap<String, String> modsMap = new HashMap<String, String>(mods.length);

		for (Mod mod : mods) {
			modsMap.put(mod.getName(), mod.getVersion());
		}

		return modsMap;
	}
}
