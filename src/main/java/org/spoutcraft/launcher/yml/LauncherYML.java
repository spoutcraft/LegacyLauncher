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
import java.util.Map;

import org.spoutcraft.launcher.Main;
import org.spoutcraft.launcher.api.util.MirrorUtils;
import org.spoutcraft.launcher.api.util.Utils;
import org.spoutcraft.launcher.api.util.YAMLFormat;
import org.spoutcraft.launcher.api.util.YAMLNode;
import org.spoutcraft.launcher.api.util.YAMLProcessor;

public class LauncherYML {
	private static volatile boolean updated = false;
	private static File launcherYML = new File(Utils.getWorkingDirectory(), "spoutcraft" + File.separator + "launcher.yml");
	private static Object key = new Object();
	private static int recommened = -1;
	private static int current = -1;
	private static int latest = -1;

	public static YAMLProcessor getLauncherYML() {
		updateLauncherYMLCache();
		YAMLProcessor config = new YAMLProcessor(launcherYML, false);
		try {
			config.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return config;
	}

	public static String getJarMD5(int build) {
		return getMD5(build, "jar");
	}

	public static String getAppMD5(int build) {
		return getMD5(build, "app");
	}

	public static String getExeMD5(int build) {
		return getMD5(build, "jar");
	}

	@SuppressWarnings("unchecked")
	public static String getMD5(int build, String type) {
		YAMLProcessor config = getLauncherYML();
		Map<Integer, YAMLNode> builds = (Map<Integer, YAMLNode>) config.getProperty("builds");
		return ((Map<String, String>) builds.get(build)).get(type);
	}

	public static void updateLauncherYMLCache() {
		if (!updated) {
			synchronized (key) {
				String urlName = MirrorUtils.getMirrorUrl("launcher.yml", "http://get.spout.org/yml/launcher.yml", null);
				System.out.println(urlName);
				if (urlName != null) {
					try {
						URL url = new URL(urlName);
						HttpURLConnection con = (HttpURLConnection) (url.openConnection());
						System.setProperty("http.agent", "");
						con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.100 Safari/534.30");
						Utils.copy(con.getInputStream(), new FileOutputStream(launcherYML));

						YAMLProcessor config = new YAMLProcessor(launcherYML, false, YAMLFormat.EXTENDED);
						config.load();
						config.setProperty("current", Main.getBuild("launcher-version"));
						// TODO REMOVE COMMENT current = config.getInt("current");
						recommened = config.getInt("recommended");
						latest = config.getInt("latest");
						config.save();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				updated = true;
			}
		}
	}
}
