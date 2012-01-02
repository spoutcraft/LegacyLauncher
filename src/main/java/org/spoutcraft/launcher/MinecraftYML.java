package org.spoutcraft.launcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.spoutcraft.launcher.config.YAMLProcessor;

public class MinecraftYML {
	private static volatile boolean updated = false;
	private static File minecraftYML = new File(PlatformUtils.getWorkingDirectory(), "spoutcraft" + File.separator + "minecraft.yml");
	private static String latest = null;
	private static String recommended = null;
	private static Object key = new Object();
	
	public static YAMLProcessor getMinecraftYML() {
		updateMinecraftYMLCache();
		YAMLProcessor config = new YAMLProcessor(minecraftYML, false);
		try {
			config.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return config;
	}
	
	public static String getLatestMinecraftVersion() {
		updateMinecraftYMLCache();
		return latest;
	}
	
	public static String getRecommendedMinecraftVersion() {
		updateMinecraftYMLCache();
		return recommended;
	}
	
	public static void setInstalledVersion(String version) {
		YAMLProcessor config = getMinecraftYML();
		config.setProperty("current", version);
		config.save();
	}
	
	public static String getInstalledVersion() {
		YAMLProcessor config = getMinecraftYML();
		return config.getString("current");
	}
	
	public static void updateMinecraftYMLCache() {
		if (!updated) {
			synchronized(key) {
				String urlName = MirrorUtils.getMirrorUrl("minecraft.yml", "http://dl.getspout.org/yml/minecraft.yml", null);
				if (urlName != null) {
					try {
						
						String current = null;
						if (minecraftYML.exists()) {
							try {
								YAMLProcessor config = new YAMLProcessor(minecraftYML, false);
								config.load();
								current = config.getString("current");
							}
							catch (Exception ex){
								ex.printStackTrace();
							}
						}
						
						URL url = new URL(urlName);
						HttpURLConnection con = (HttpURLConnection)(url.openConnection());
						System.setProperty("http.agent", "");
						con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.100 Safari/534.30");
						GameUpdater.copy(con.getInputStream(), new FileOutputStream(minecraftYML));
						
						YAMLProcessor config = new YAMLProcessor(minecraftYML, false);
						config.load();
						latest = config.getString("latest");
						recommended = config.getString("recommended");
						if (current != null) {
							config.setProperty("current", current);
							config.save();
						}
						
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				}
				updated = true;
			}
		}
	}
}
