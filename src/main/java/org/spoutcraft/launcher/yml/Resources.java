package org.spoutcraft.launcher.yml;

import java.io.File;
import java.util.Map;

import org.spoutcraft.launcher.api.util.Utils;
import org.spoutcraft.launcher.api.util.YAMLProcessor;

public enum Resources implements YAMLResource{
	Launcher ("http://get.spout.org/launcher.yml",
			new File(Utils.getWorkingDirectory(), "config" + File.separator + "launcher.yml"),
			null),

	Libraries ("http://get.spout.org/libraries.yml",
			new File(Utils.getWorkingDirectory(), "config" + File.separator + "libraries.yml"),
			null),

	Minecraft ("http://get.spout.org/minecraft.yml",
			new File(Utils.getWorkingDirectory(), "config" + File.separator + "minecraft.yml"),
			new PersistCurrentResourceAction()),

	Special ("http://get.spout.org/special.yml",
			new File(Utils.getWorkingDirectory(), "config" + File.separator + "special.yml"),
			null),

	VIP ("http://get.spout.org/vip.yml",
			new File(Utils.getWorkingDirectory(), "config" + File.separator + "vip.yml"),
			null),

	Spoutcraft ("http://get.spout.org/spoutcraft.yml",
			new File(Utils.getWorkingDirectory(), "config" + File.separator + "spoutcraft.yml"),
			new PersistCurrentResourceAction()),

	Assets ("http://get.spout.org/assets.yml",
			new File(Utils.getWorkingDirectory(), "config" + File.separator + "assets.yml"),
			null),
	;

	final BaseYAMLResource resource;
	private Resources(String url, File directory, ResourceAction action) {
		this.resource = new BaseYAMLResource(url, directory, action);
	}

	public YAMLProcessor getYAML() {
		return resource.getYAML();
	}

	public boolean updateYAML() {
		return resource.updateYAML();
	}

	@SuppressWarnings("unchecked")
	public static String getLibraryMD5(String library, String version) {
		YAMLProcessor config = Resources.Libraries.getYAML();
		Map<String, Object> libraries = (Map<String, Object>) config.getProperty(library);
		Map<String, String> versions = (Map<String, String>) libraries.get("versions");
		String result = versions.get(version);
		if (result == null) {
			try {
				result = versions.get(Double.parseDouble(version));
			} catch (NumberFormatException ignore) { }
		}
		return result;
	}

	public static String getLatestMinecraftVersion() {
		return Resources.Minecraft.getYAML().getString("latest");
	}

	public static String getRecommendedMinecraftVersion() {
		return Resources.Minecraft.getYAML().getString("recommended");
	}

	public static void setInstalledVersion(String version) {
		YAMLProcessor config = Resources.Minecraft.getYAML();
		config.setProperty("current", version);
		config.save();
	}

	public static String getInstalledVersion() {
		return Resources.Minecraft.getYAML().getString("current");
	}
}

class PersistCurrentResourceAction implements ResourceAction {
	String previousValue = null;

	public void beforeAction(YAMLProcessor previous) {
		previousValue = previous.getString("current", null);
	}

	public void afterAction(YAMLProcessor current) {
		current.setProperty("current", previousValue);
		current.save();
		previousValue = null;
	}
	
}