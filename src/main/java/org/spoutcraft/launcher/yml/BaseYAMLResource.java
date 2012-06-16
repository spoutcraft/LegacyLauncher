package org.spoutcraft.launcher.yml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.spoutcraft.launcher.api.util.YAMLProcessor;

public class BaseYAMLResource implements YAMLResource{
	private final Logger logger = Logger.getLogger("launcher");
	private YAMLProcessor cached = null;
	private final File localCache;
	private final String url;
	private final ResourceAction action;
	public BaseYAMLResource(String url, File file, ResourceAction action) {
		this.url = url;
		this.localCache = file;
		this.action = action;
	}

	public synchronized YAMLProcessor getYAML() {
		updateYAML();
		return cached;
	}

	public synchronized boolean updateYAML() {
		if (cached == null) {
			InputStream stream = null;
			FileOutputStream fout = null;
			try {
				//Pre resource action
				if (localCache.exists() && action != null) {
					try {
						YAMLProcessor previous = new YAMLProcessor(localCache, false);
						previous.load();
						action.beforeAction(previous);
					} catch (Exception e) {
						logger.log(Level.WARNING, "Failed to execute pre resource action", e);
					}
				}
	
				//Setup url
				URL url = new URL(this.url);
				HttpURLConnection conn = (HttpURLConnection) (url.openConnection());
				System.setProperty("http.agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.162 Safari/535.19");
				conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.162 Safari/535.19");
				
				//Copy file
				stream = conn.getInputStream();
				fout = new FileOutputStream(localCache);
				fout.getChannel().transferFrom(Channels.newChannel(stream), 0, Integer.MAX_VALUE);
	
				//Setup cached processor
				cached = new YAMLProcessor(localCache, false);
				cached.load();
				
				//post resource action
				if (action != null) {
					try {
						action.afterAction(cached);
					} catch (Exception e) {
						logger.log(Level.WARNING, "Failed to execute post resource action", e);
					}
				}
				
				return true;
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Failed to update YAML file with " + url, e);
			} finally {
				if (stream != null) {
					try {
						stream.close();
					} catch (IOException ignore) { }
				}
				if (fout != null) {
					try {
						fout.close();
					} catch (IOException ignore) { }
				}
			}
		}
		return false;
	}
}
