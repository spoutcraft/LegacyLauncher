package org.spoutcraft.launcher.technic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;
import org.spoutcraft.launcher.exceptions.RestfulAPIException;
import org.spoutcraft.launcher.rest.Library;
import org.spoutcraft.launcher.rest.Versions;
import org.spoutcraft.launcher.util.MirrorUtils;

public class Modpack {

	@JsonProperty("libraries")
	private String libraries;
	@JsonProperty("minecraft")
	private String minecraftVersion;
	@JsonProperty("forge")
	private String forgeVersion;
	@JsonProperty("mods")
	private Map<String, String> mods;

	private String name;
	private String build;

	public Modpack setInfo(String name, String build) {
		this.name = name;
		this.build = build;
		return this;
	}

	//TODO Make these two library methods the same somehow
	public List<Library> getLibraries() {
		return new ArrayList<Library>();
	}

	public String getMinecraftVersion() {
		return minecraftVersion;
	}

	public String getMinecraftURL(String user) {
		return "http://s3.amazonaws.com/MinecraftDownload/minecraft.jar?user=" + user + "&ticket=1";
	}

	public String getPatchURL() {
		String mirrorURL = "patch/minecraft_";
		mirrorURL += Versions.getLatestMinecraftVersion();
		mirrorURL += "-" + getMinecraftVersion() + ".patch";
		String fallbackURL = "http://get.spout.org/patch/minecraft_";
		fallbackURL += Versions.getLatestMinecraftVersion();
		fallbackURL += "-" + getMinecraftVersion() + ".patch";
		return MirrorUtils.getMirrorUrl(mirrorURL, fallbackURL);
	}

	public String getForgeVersion() {
		return forgeVersion;
	}

	public List<Mod> getMods() {
		List<Mod> modList = new ArrayList<Mod>(mods.size());
		for (String name : mods.keySet()) {
			modList.add(new Mod(name, mods.get(name)));
		}
		return modList;
	}

	public String getName() {
		return name;
	}

	public String getBuild() {
		return build;
	}

	@Override
	public String toString() {
		return "{ Modpack [name: " + name + ", build: " + build + ", libraries: " + libraries + ", minecraft: " + minecraftVersion + ", forge: " + forgeVersion + ", mods: " + mods + "] }";
	}

	public String getMD5() throws RestfulAPIException {
		return TechnicRestAPI.getModpackMD5(this.getName());
	}

}
