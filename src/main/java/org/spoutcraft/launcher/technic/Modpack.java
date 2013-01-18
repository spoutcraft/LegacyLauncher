package org.spoutcraft.launcher.technic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;
import org.spoutcraft.launcher.rest.Library;
import org.spoutcraft.launcher.rest.Versions;
import org.spoutcraft.launcher.util.MirrorUtils;

public class Modpack {

	@JsonProperty("libraries")
	private String[] libraries;
	@JsonProperty("minecraft")
	private String minecraftVersion;
	@JsonProperty("forge")
	private String forgeVersion;
	@JsonProperty("mods")
	private Mod[] mods;

	private String build;

	public Modpack setBuild(String build) {
		this.build = build;
		return this;
	}

	//TODO Make these two library methods the same
	public List<Library> getLibraries() {
		return new ArrayList<Library>();
	}

	public List<String> getModLibraries() {
		return Arrays.asList(libraries);
	}

	public String getMinecraftVersion() {
		return minecraftVersion;
	}

	public String getLatestMinecraftVersion() {
		return Versions.getLatestMinecraftVersion();
	}

	public String getMinecraftURL(String user) {
		return "http://s3.amazonaws.com/MinecraftDownload/minecraft.jar?user=" + user + "&ticket=1";
	}

	public String getPatchURL() {
		String mirrorURL = "patch/minecraft_";
		mirrorURL += getLatestMinecraftVersion();
		mirrorURL += "-" + getMinecraftVersion() + ".patch";
		String fallbackURL = "http://get.spout.org/patch/minecraft_";
		fallbackURL += getLatestMinecraftVersion();
		fallbackURL += "-" + getMinecraftVersion() + ".patch";
		return MirrorUtils.getMirrorUrl(mirrorURL, fallbackURL);
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

	public String getBuild() {
		return build;
	}
}
