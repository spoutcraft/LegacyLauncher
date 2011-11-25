package org.spoutcraft.launcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import org.spoutcraft.launcher.async.DownloadListener;

public class MirrorUtils {
	private static ArrayList<String> mirrors = new ArrayList<String>();
	private static final Random rand = new Random();
	public static String getMirrorUrl(String mirrorURI, String fallbackUrl, DownloadListener listener) {
		try {
			if (mirrors.size() == 0) {
				updateMirrors();
			}
			int random = rand.nextInt(10 * mirrors.size());
			int index = random / 10;
			float progress = 0F;
			//Test for bad, down mirrors
			for (int i = index; i < mirrors.size() + index; i++) {
				int j = i;
				if (j >= mirrors.size()) j-= mirrors.size();
				String mirror = "http://" + mirrors.get(j) + "/" + mirrorURI;
				if (isAddressReachable(mirror)) {
					System.out.println("Using mirror: " + mirror);
					if (listener != null) {
						listener.stateChanged("Contacting Mirrors...", 100F);
					}
					return mirror;
				}
				else {
					progress += 100F / mirrors.size();
					if (listener != null) {
						listener.stateChanged("Contacting Mirrors...", progress);
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		System.err.println("All mirrors failed, reverting to default");
		return fallbackUrl;
	}
	
	public static boolean isAddressReachable(String url) {
		try {
			URL test = new URL(url);
			HttpURLConnection.setFollowRedirects(false);
			HttpURLConnection urlConnect = (HttpURLConnection)test.openConnection();
			urlConnect.setRequestMethod("HEAD");
			return (urlConnect.getResponseCode() == HttpURLConnection.HTTP_OK);
		} catch (Exception e) {
			return false;
		}
	}
	
	public static void updateMirrors() throws IOException {
		URL url = new URL("http://cdn.getspout.org/mirrors.html");
		HttpURLConnection con = (HttpURLConnection)(url.openConnection());
		System.setProperty("http.agent", "");
		con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.100 Safari/534.30");
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		mirrors.clear();
		String data = "";
		while ((data = in.readLine()) != null) {
			mirrors.add(data);
		}
		in.close();
	}
}
