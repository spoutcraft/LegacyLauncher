package org.spoutcraft.launcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.spoutcraft.launcher.config.YAMLProcessor;

public class VipYML {
	private static volatile boolean updated = false;
	private static File vipYML = new File(PlatformUtils.getWorkingDirectory(), "spoutcraft" + File.separator + "vip.yml");
	private static Object key = new Object();
	
	public static YAMLProcessor getVipYML() {
		updateVipYMLCache();
		YAMLProcessor config = new YAMLProcessor(vipYML, false);
		try {
			config.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return config;
	}
	
	public static void updateVipYMLCache() {
		if (!updated) {
			synchronized(key) {
				String urlName = MirrorUtils.getMirrorUrl("vip.yml", "http://dl.getspout.org/yml/vip.yml", null);
				if (urlName != null) {
					try {
						
						String current = null;
						if (vipYML.exists()) {
							try {
								YAMLProcessor config = new YAMLProcessor(vipYML, false);
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
						GameUpdater.copy(con.getInputStream(), new FileOutputStream(vipYML));
						
						YAMLProcessor config = new YAMLProcessor(vipYML, false);
						config.load();
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
