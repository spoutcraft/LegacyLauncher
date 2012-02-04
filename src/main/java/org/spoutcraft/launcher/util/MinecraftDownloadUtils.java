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

package org.spoutcraft.launcher.util;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.spoutcraft.diff.JBPatch;
import org.spoutcraft.launcher.api.Launcher;
import org.spoutcraft.launcher.api.util.Download;
import org.spoutcraft.launcher.api.util.DownloadListener;
import org.spoutcraft.launcher.api.util.FileType;
import org.spoutcraft.launcher.api.util.Utils;
import org.spoutcraft.launcher.api.util.YAMLProcessor;
import org.spoutcraft.launcher.yml.SpoutcraftBuild;
import org.spoutcraft.launcher.yml.SpoutcraftYML;

public class MinecraftDownloadUtils {
	public static void downloadMinecraft(String user, String output, SpoutcraftBuild build, DownloadListener listener) throws IOException {
		int tries = 3;
		File outputFile = null;
		while (tries > 0) {
			System.out.println("Starting download of minecraft, with " + tries + " tries remaining");
			tries--;
			Download download = new Download(build.getMinecraftURL(user), output);
			download.setListener(listener);
			download.run();
			if (!download.isSuccess()) {
				if (download.getOutFile() != null) {
					download.getOutFile().delete();
				}
				System.err.println("Download of minecraft failed!");
				listener.stateChanged("Download Failed, retries remaining: " + tries, 0F);
			} else {
				String minecraftMD5 = MD5Utils.getMD5(FileType.MINECRAFT, build.getLatestMinecraftVersion());
				String resultMD5 = MD5Utils.getMD5(download.getOutFile());
				System.out.println("Expected MD5: " + minecraftMD5 + " Result MD5: " + resultMD5);
				if (resultMD5.equals(minecraftMD5)) {
					if (!build.getLatestMinecraftVersion().equals(build.getMinecraftVersion())) {

						File patch = new File(Utils.getWorkingDirectory(), "mc.patch");
						Download patchDownload = DownloadUtils.downloadFile(build.getPatchURL(), patch.getPath(), null, null, listener);
						if (patchDownload.isSuccess()) {
							File patchedMinecraft = new File(Launcher.getGameUpdater().getUpdateDir(), "patched_minecraft.jar");
							patchedMinecraft.delete();
							JBPatch.bspatch(download.getOutFile(), patchedMinecraft, patch);
							String minecraft181MD5 = MD5Utils.getMD5(FileType.MINECRAFT, build.getMinecraftVersion());
							resultMD5 = MD5Utils.getMD5(patchedMinecraft);

							if (minecraft181MD5.equals(resultMD5)) {
								outputFile = download.getOutFile();
								download.getOutFile().delete();
								Utils.copy(patchedMinecraft, download.getOutFile());
								patchedMinecraft.delete();
								patch.delete();
								break;
							}
						}
					} else {
						outputFile = download.getOutFile();
						break;
					}
				}
			}
		}
		if (outputFile == null) {
			throw new IOException("Failed to download minecraft");
		}
		Utils.copy(outputFile, new File(Launcher.getGameUpdater().getBinCacheDir(), "minecraft_" + build.getMinecraftVersion() + ".jar"));
	}

	@SuppressWarnings("unchecked")
	public static String[] getSpoutcraftBuilds() {
		YAMLProcessor config = SpoutcraftYML.getSpoutcraftYML();
		Map<Integer, Object> builds = (Map<Integer, Object>) config.getProperty("builds");
		int latest = config.getInt("latest", -1);
		int recommended = config.getInt("recommended", -1);

		if (builds != null) {
			String[] results = new String[builds.size()];
			int index = 0;
			for (Integer i : builds.keySet()) {
				results[index] = i.toString();
				Map<String, Object> map = (Map<String, Object>) builds.get(i);
				String version = String.valueOf(map.get("minecraft"));
				results[index] += "| " + version;
				if (i.intValue() == latest) {
					results[index] += " | Latest";
				}
				if (i.intValue() == recommended) {
					results[index] += " | Rec. Build";
				}
				index++;
			}
			return results;
		}
		return null;
	}
}
