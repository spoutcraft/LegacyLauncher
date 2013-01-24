package org.spoutcraft.launcher.technic;

import java.io.File;
import java.io.FileNotFoundException;

import org.spoutcraft.launcher.api.Launcher;
import org.spoutcraft.launcher.util.Utils;
import org.spoutcraft.launcher.yml.YAMLFormat;
import org.spoutcraft.launcher.yml.YAMLProcessor;

public class ModpackSettings {
	public ModpackInfo modpackInfo;
	private YAMLProcessor yaml;
	
	public ModpackSettings(ModpackInfo modpackInfo) {
		this.modpackInfo = modpackInfo;
		setupSettings();
	}
	
	protected void setupSettings() {
		File directory = new File(Utils.getLauncherDirectory() + File.separator, modpackInfo.getName());
		directory.mkdirs();
		File file = new File(Utils.getLauncherDirectory() + File.separator + modpackInfo.getName(), "settings.yml");
		
		try {
			file.createNewFile();
		} catch (Exception e) { 
			e.printStackTrace();
		}
		
		yaml = new YAMLProcessor(file, false, YAMLFormat.EXTENDED);
		
		try {
			this.yaml.load();
		} catch (FileNotFoundException ignore) {
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (getBuild() == null) {
			setBuild(modpackInfo.getRecommended());
		}
	}
	
	public YAMLProcessor getYAML() {
		return yaml;
	}
	
	public void setBuild(String build) {
		yaml.setProperty("modpack.build", build);
	}
	
	public String getBuild() {
		return (String) yaml.getProperty("modpack.build");
	}
	

}
