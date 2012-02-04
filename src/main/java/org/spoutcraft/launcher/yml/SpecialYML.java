/*
 * This file is part of Launcher (http://www.spout.org/).
 *
 * Launcher is licensed under the SpoutDev License Version 1.
 *
 * Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Launcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */

package org.spoutcraft.launcher.yml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.spoutcraft.launcher.api.util.Utils;
import org.spoutcraft.launcher.api.util.YAMLProcessor;
import org.spoutcraft.launcher.api.util.MirrorUtils;

public class SpecialYML {
	private static volatile boolean updated = false;
	private static File specialYML = new File(Utils.getWorkingDirectory(), "spoutcraft" + File.separator + "special.yml");
	private static Object key = new Object();

	public static YAMLProcessor getSpecialYML() {
		updateSpecialYMLCache();
		YAMLProcessor config = new YAMLProcessor(specialYML, false);
		try {
			config.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return config;
	}

	public static void updateSpecialYMLCache() {
		if (!updated) {
			synchronized (key) {
				String urlName = MirrorUtils.getMirrorUrl("special.yml", "http://dl.getspout.org/yml/special.yml", null);
				if (urlName != null) {
					try {
						String current = null;
						if (specialYML.exists()) {
							try {
								YAMLProcessor config = new YAMLProcessor(specialYML, false);
								config.load();
								current = config.getString("current");
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}

						URL url = new URL(urlName);
						HttpURLConnection con = (HttpURLConnection) (url.openConnection());
						System.setProperty("http.agent", "");
						con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.100 Safari/534.30");
						Utils.copy(con.getInputStream(), new FileOutputStream(specialYML));

						YAMLProcessor config = new YAMLProcessor(specialYML, false);
						config.load();
						if (current != null) {
							config.setProperty("current", current);
							config.save();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				updated = true;
			}
		}
	}
}
