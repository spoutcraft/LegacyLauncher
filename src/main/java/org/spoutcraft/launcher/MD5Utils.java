package org.spoutcraft.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.bukkit.util.config.Configuration;

public class MD5Utils {
	private static boolean updated = false;
	private static File minecraftYML = new File(PlatformUtils.getWorkingDirectory(), "minecraft.yml");
	private static String latest = null;
	@SuppressWarnings("unused")
	private static String recommended = null;
	
	public static String getMD5(File file){
		try {
			return DigestUtils.md5Hex(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getMD5(FileType type) {
		updateMinecraftYMLCache();
		return getMD5(type, latest);
	}
	
	private static Configuration getMinecraftYML() {
		updateMinecraftYMLCache();
		Configuration config = new Configuration(minecraftYML);
		config.load();
		return config;
	}
	
	@SuppressWarnings("unchecked")
	public static String getMD5(FileType type, String version) {
		Configuration config = getMinecraftYML();
		Map<String, Map<String, String>> builds = (Map<String, Map<String, String>>) config.getProperty("versions");
		if (builds.containsKey(version)) {
			Map<String, String> files = builds.get(version);
			return files.get(type.name());
		}
		return null;
	}
	
	public static void updateMinecraftYMLCache() {
		if (!updated) {
			String urlName = MirrorUtils.getMirrorUrl("minecraft.yml", "http://mirror3.getspout.org/minecraft.yml", null);
			if (urlName != null) {
				try {
					URL url = new URL(urlName);
					HttpURLConnection con = (HttpURLConnection)(url.openConnection());
					System.setProperty("http.agent", "");
					con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.100 Safari/534.30");
					GameUpdater.copy(con.getInputStream(), new FileOutputStream(minecraftYML));
					
					Configuration config = new Configuration(minecraftYML);
					config.load();
					latest = config.getString("latest");
					recommended = config.getString("recommended");
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
			updated = true;
		}
	}

}
