package org.spoutcraft.launcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import org.bukkit.util.config.Configuration;

public class SpoutcraftYML {
	private static volatile boolean updated = false;
	private static File spoutcraftYML = new File(PlatformUtils.getWorkingDirectory(), "spoutcraft" + File.separator + "spoutcraft.yml");
	private static Object key = new Object();

	public static Configuration getSpoutcraftYML() {
		updateSpoutcraftYMLCache();
		Configuration config = new Configuration(spoutcraftYML);
		config.load();
		return config;
	}
	
	public static void updateSpoutcraftYMLCache() {
		if (!updated) {
			synchronized(key) {
				String urlName = MirrorUtils.getMirrorUrl("spoutcraft.yml", "http://mirror3.getspout.org/spoutcraft.yml", null);
				if (urlName != null) {
	
					try {
						int selected = -1;
						if (spoutcraftYML.exists()) {
							try {
								Configuration config = new Configuration(spoutcraftYML);
								config.load();
								selected = config.getInt("current", -1);
							}
							catch (Exception ex){
								ex.printStackTrace();
							}
						}
	
						URL url = new URL(urlName);
						HttpURLConnection con = (HttpURLConnection)(url.openConnection());
						System.setProperty("http.agent", "");
						con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.100 Safari/534.30");
						GameUpdater.copy(con.getInputStream(), new FileOutputStream(spoutcraftYML));
	
						Configuration config = new Configuration(spoutcraftYML);
						config.load();
						config.setProperty("current", selected);
						config.setProperty("launcher", Main.build);
						config.save();
						
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
