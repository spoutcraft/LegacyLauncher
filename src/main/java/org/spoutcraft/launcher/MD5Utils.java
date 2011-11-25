package org.spoutcraft.launcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Utils {
	private static List<String> cache = new ArrayList<String>(100);
	
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
		return getMD5(type, "current");
	}
	
	public static String getMD5(FileType type, String version) {
		updateMD5Cache();
		version += ":";
		int start = -1;
		int end = cache.size();
		for (int i = 0; i < cache.size(); i++) {
			String line = cache.get(i);
			if (line.equals(version)) {
				start = i;
			}
			else if (start > -1 && !line.startsWith(" ")) {
				end = i;
				break;
			}
		}
		
		for (int i = start + 1; i < end; i++) {
			String line = cache.get(i).trim();
			String split[] = line.split(":");
			if (split[0].equals(type.name())) {
				return split[1].trim();
			}
		}
		
		return null;
	}
	
	private static void updateMD5Cache() {
		if (cache.size() > 0) {
			return;
		}
		String urlName = MirrorUtils.getMirrorUrl("minecraft.yml", "http://mirror3.getspout.org/minecraft.yml", null);
		if (urlName != null) {
			BufferedReader in = null;
			try {
				URL url = new URL(urlName);
				HttpURLConnection con = (HttpURLConnection)(url.openConnection());
				System.setProperty("http.agent", "");
				con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.100 Safari/534.30");
				in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				cache.clear();
				String data = "";
				while ((data = in.readLine()) != null) {
					cache.add(data);
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException ignore) {}
				}
			}
		}
	}
	
	

}
