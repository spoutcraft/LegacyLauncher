package org.spoutcraft.launcher;

import java.util.Map;

import org.spoutcraft.launcher.config.YAMLProcessor;

import org.spoutcraft.launcher.async.DownloadListener;

public class SpoutcraftBuild {
	private String minecraftVersion;
	private String latestVersion;
	private int build;
	Map<String, Object> libraries;
	private DownloadListener listener = null;
	private String hash;

	private SpoutcraftBuild(String minecraft, String latest, int build, Map<String, Object> libraries, String hash) {
		this.minecraftVersion = minecraft;
		this.latestVersion = latest;
		this.build = build;
		this.libraries = libraries;
		this.hash = hash;
	}
    
	public String getMD5() {
		return hash;
	}

	public int getBuild() {
		return build;
	}

	public String getMinecraftVersion() {
		return minecraftVersion;
	}

	public String getLatestMinecraftVersion() {
		return latestVersion;
	}

	public String getMinecraftURL(String user) {
		return "http://s3.amazonaws.com/MinecraftDownload/minecraft.jar?user=" + user + "&ticket=1";
	}

	public String getSpoutcraftURL() {
		return MirrorUtils.getMirrorUrl("Spoutcraft/" + build + "/spoutcraft-dev-SNAPSHOT.jar", null, listener);
	}

	public void setDownloadListener(DownloadListener listener) {
		this.listener = listener;
	}

	public void install() {
		YAMLProcessor config = SpoutcraftYML.getSpoutcraftYML();
		config.setProperty("current", getBuild());
		config.save();
	}

	public int getInstalledBuild() {
		YAMLProcessor config = SpoutcraftYML.getSpoutcraftYML();
		return config.getInt("current", -1);
	}

	public String getPatchURL() {
		String mirrorURL = "/Patches/Minecraft/minecraft_";
		mirrorURL += getLatestMinecraftVersion();
		mirrorURL += "-" + getMinecraftVersion() + ".patch";
		String fallbackURL = "http://dl.getspout.org/Patches/Minecraft/minecraft_";
		fallbackURL += getLatestMinecraftVersion();
		fallbackURL += "-" + getMinecraftVersion() + ".patch";
		return MirrorUtils.getMirrorUrl(mirrorURL, fallbackURL, listener);
	}
	
	public Map<String, Object> getLibraries() {
		return libraries;
	}

	@SuppressWarnings("unchecked")
	public static SpoutcraftBuild getSpoutcraftBuild() {
		YAMLProcessor config = SpoutcraftYML.getSpoutcraftYML();
		Map<Integer, Object> builds = (Map<Integer, Object>) config.getProperty("builds");
		int latest = config.getInt("latest", -1);
		int recommended = config.getInt("recommended", -1);
		int selected = SettingsUtil.getSelectedBuild();
		if (SettingsUtil.isRecommendedBuild()) {
			selected = recommended;
		} else if (SettingsUtil.isDevelopmentBuild()) {
			selected = latest;
		}

		Map<Object, Object> build = (Map<Object, Object>) builds.get(selected);
		Map<String, Object> libs = (Map<String, Object>) build.get("libraries");
		String hash = String.valueOf(build.get("hash"));
		return new SpoutcraftBuild(String.valueOf(build.get("minecraft")), MinecraftYML.getLatestMinecraftVersion(), selected, libs, hash);
	}
}
