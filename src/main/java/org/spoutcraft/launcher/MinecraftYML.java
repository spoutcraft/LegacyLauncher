package org.spoutcraft.launcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.bukkit.util.config.Configuration;

public class MinecraftYML {
	private static boolean updated = false;
	private static File minecraftYML = new File(PlatformUtils.getWorkingDirectory(), "spoutcraft" + File.separator + "minecraft.yml");
	private static String latest = null;
	private static String recommended = null;
	
	public static Configuration getMinecraftYML() {
		updateMinecraftYMLCache();
		Configuration config = new Configuration(minecraftYML);
		config.load();
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
		Configuration config = getMinecraftYML();
		config.setProperty("current", version);
	}
	
	public static String getInstalledVersion() {
		Configuration config = getMinecraftYML();
		return config.getString("current");
	}
	
	public static void updateMinecraftYMLCache() {
		if (!updated) {
			String urlName = MirrorUtils.getMirrorUrl("minecraft.yml", "http://mirror3.getspout.org/minecraft.yml", null);
			if (urlName != null) {
				try {
					
					String current = null;
					if (minecraftYML.exists()) {
						try {
							Configuration config = new Configuration(minecraftYML);
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
					
					Configuration config = new Configuration(minecraftYML);
					config.load();
					latest = config.getString("latest");
					recommended = config.getString("recommended");
					if (current != null) {
						config.setProperty("current", current);
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
