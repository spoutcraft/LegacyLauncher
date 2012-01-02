package org.spoutcraft.launcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.spoutcraft.launcher.config.YAMLProcessor;

public class LibrariesYML {
	private static volatile boolean updated = false;
	private static File librariesYML = new File(PlatformUtils.getWorkingDirectory(), "spoutcraft" + File.separator + "libraries.yml");
	private static Object key = new Object();

	public static YAMLProcessor getLibrariesYML() {
		updateLibrariesYMLCache();
		YAMLProcessor config = new YAMLProcessor(librariesYML, false);
		try {
			config.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return config;
	}
	
	public static void updateLibrariesYMLCache() {
		if (!updated) {
			synchronized(key) {
				String urlName = MirrorUtils.getMirrorUrl("libraries.yml", "http://mirror3.getspout.org/libraries.yml", null);
				if (urlName != null) {
	
					try {
						URL url = new URL(urlName);
						HttpURLConnection con = (HttpURLConnection)(url.openConnection());
						System.setProperty("http.agent", "");
						con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.100 Safari/534.30");
						GameUpdater.copy(con.getInputStream(), new FileOutputStream(librariesYML));
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				}
				updated = true;
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static String getMD5(String library, String version) {
		YAMLProcessor config = getLibrariesYML();
		Map<String, Object> libraries = (Map<String, Object>) config.getProperty(library);
		Map<String, String> versions = (Map<String, String>) libraries.get("versions");
		String result = versions.get(version);
		if (result == null) {
			try {
				result = versions.get(Double.parseDouble(version));
			}
			catch (NumberFormatException ignore) {}
		}
		return result;
	}

}
