/*
 * This file is part of Spoutcraft.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * Spoutcraft is licensed under the Spout License Version 1.
 *
 * Spoutcraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spoutcraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
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


public class BaseYAMLResource implements YAMLResource {
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
