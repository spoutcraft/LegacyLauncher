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
package org.spoutcraft.launcher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.JarFile;

import org.spoutcraft.launcher.api.Event;
import org.spoutcraft.launcher.api.GameUpdater;
import org.spoutcraft.launcher.api.Launcher;
import org.spoutcraft.launcher.api.skin.Skin;
import org.spoutcraft.launcher.api.skin.SkinManager;
import org.spoutcraft.launcher.api.skin.gui.LoginFrame;
import org.spoutcraft.launcher.api.util.Download;
import org.spoutcraft.launcher.api.util.FileType;
import org.spoutcraft.launcher.api.util.FileUtils;
import org.spoutcraft.launcher.api.util.MirrorUtils;
import org.spoutcraft.launcher.api.util.Utils;
import org.spoutcraft.launcher.api.util.YAMLNode;
import org.spoutcraft.launcher.exceptions.NoMirrorsAvailableException;
import org.spoutcraft.launcher.exceptions.UnsupportedOSException;
import org.spoutcraft.launcher.util.DownloadUtils;
import org.spoutcraft.launcher.util.MD5Utils;
import org.spoutcraft.launcher.util.MinecraftDownloadUtils;
import org.spoutcraft.launcher.yml.LibrariesYML;
import org.spoutcraft.launcher.yml.MinecraftYML;
import org.spoutcraft.launcher.yml.SpoutcraftBuild;
import org.spoutcraft.launcher.yml.SpoutcraftYML;

public class SimpleGameUpdater extends GameUpdater {
	// Debug variables \\
	private long validationTime;

	public SimpleGameUpdater() {
		super();
	}

	public boolean isSpoutcraftUpdateAvailible() {
		if (!Utils.getWorkingDirectory().exists()) {
			return true;
		}
		if (!getSpoutcraftDir().exists()) {
			return true;
		}

		SpoutcraftBuild build = SpoutcraftBuild.getSpoutcraftBuild();
		Map<String, Object> libraries = build.getLibraries();
		int steps = libraries.size() + 2;
		float progress = 100F;

		if (build.getBuild() != build.getInstalledBuild()) {
			return true;
		}
		stateChanged("Checking for Spoutcraft update...", progress / steps);
		progress += 100F;
		File spoutcraft = new File(getBinDir(), "spoutcraft.jar");
		if (!spoutcraft.exists()) {
			return true;
		}
		stateChanged("Checking for Spoutcraft update...", progress / steps);
		progress += 100F;
		File libDir = new File(getBinDir(), "lib");
		libDir.mkdir();

		Iterator<Entry<String, Object>> i = libraries.entrySet().iterator();
		while (i.hasNext()) {
			Entry<String, Object> lib = i.next();
			File libraryFile = new File(libDir, lib.getKey() + ".jar");
			if (!libraryFile.exists()) {
				return true;
			}
			stateChanged("Checking for Spoutcraft update...", progress / steps);
			progress += 100F;
		}

		return false;
	}

	public boolean isMinecraftUpdateAvailible() {
		int steps = 7;
		if (!getBinDir().exists()) {
			return true;
		}
		stateChanged("Checking for Minecraft Update...", 100F / steps);
		if (!new File(getBinDir(), "natives").exists()) {
			return true;
		}
		stateChanged("Checking for Minecraft Update...", 200F / steps);
		File minecraft = new File(getBinDir(), "minecraft.jar");
		if (!minecraft.exists()) {
			return true;
		}
		stateChanged("Checking for Minecraft Update...", 300F / steps);
		File lib = new File(getBinDir(), "jinput.jar");
		if (!lib.exists()) {
			return true;
		}
		stateChanged("Checking for Minecraft Update...", 400F / steps);
		lib = new File(getBinDir(), "lwjgl.jar");
		if (!lib.exists()) {
			return true;
		}
		stateChanged("Checking for Minecraft Update...", 500F / steps);
		lib = new File(getBinDir(), "lwjgl_util.jar");
		if (!lib.exists()) {
			return true;
		}
		stateChanged("Checking for Minecraft Update...", 600F / steps);
		SpoutcraftBuild build = SpoutcraftBuild.getSpoutcraftBuild();
		String installed = MinecraftYML.getInstalledVersion();
		stateChanged("Checking for Minecraft Update...", 700F / steps);
		String required = build.getMinecraftVersion();
		return !installed.equals(required);
	}

	@Override
	public void updateMinecraft() throws IOException {
		getBinDir().mkdir();
		getBinCacheDir().mkdir();
		if (getUpdateDir().exists()) {
			FileUtils.deleteDirectory(getUpdateDir());
		}
		getUpdateDir().mkdir();

		String minecraftMD5 = MD5Utils.getMD5(FileType.MINECRAFT);
		String jinputMD5 = MD5Utils.getMD5(FileType.JINPUT);
		String lwjglMD5 = MD5Utils.getMD5(FileType.LWJGL);
		String lwjgl_utilMD5 = MD5Utils.getMD5(FileType.LWJGL);

		SpoutcraftBuild build = SpoutcraftBuild.getSpoutcraftBuild();

		// Processs minecraft.jar \\
		System.out.println("Spoutcraft Build: " + build.getBuild() + "Minecraft Version" + build.getMinecraftVersion());
		File mcCache = new File(getBinCacheDir(), "minecraft_" + build.getMinecraftVersion() + ".jar");
		if (!mcCache.exists() || !minecraftMD5.equals(MD5Utils.getMD5(mcCache))) {
			String minecraftURL = baseURL + "minecraft.jar?user=" + getMinecraftUser() + "&ticket=" + getDownloadTicket();
			String output = getUpdateDir() + File.separator + "minecraft.jar";
			MinecraftDownloadUtils.downloadMinecraft(minecraftURL, output, build, getDownloadListener());
		}
		Utils.copy(mcCache, new File(getBinDir(), "minecraft.jar"));

		File nativesDir = new File(getBinDir().getPath(), "natives");
		nativesDir.mkdir();

		// Process other Downloads
		mcCache = new File(getBinCacheDir(), "jinput.jar");
		if (!mcCache.exists() || !jinputMD5.equals(MD5Utils.getMD5(mcCache))) {
			DownloadUtils.downloadFile(getNativesUrl() + "jinput.jar", getBinDir().getPath() + File.separator + "jinput.jar", "jinput.jar");
		} else {
			Utils.copy(mcCache, new File(getBinDir(), "jinput.jar"));
		}



		mcCache = new File(getBinCacheDir(), "lwjgl.jar");
		if (!mcCache.exists() || !lwjglMD5.equals(MD5Utils.getMD5(mcCache))) {
			DownloadUtils.downloadFile(getNativesUrl() + "lwjgl.jar", getBinDir().getPath() + File.separator + "lwjgl.jar", "lwjgl.jar");
		} else {
			Utils.copy(mcCache, new File(getBinDir(), "lwjgl.jar"));
		}

		mcCache = new File(getBinCacheDir(), "lwjgl_util.jar");
		if (!mcCache.exists() || !lwjgl_utilMD5.equals(MD5Utils.getMD5(mcCache))) {
			DownloadUtils.downloadFile(getNativesUrl() + "lwjgl_util.jar", getBinDir().getPath() + File.separator + "lwjgl_util.jar", "lwjgl_util.jar");
		} else {
			Utils.copy(mcCache, new File(getBinDir(), "lwjgl_util.jar"));
		}

		try {
			getNatives();
		} catch (Exception e) {
			e.printStackTrace();
		}

		stateChanged("Extracting Files...", 0);

		MinecraftYML.setInstalledVersion(build.getMinecraftVersion());
	}

	public String getNativesUrl() {
		if (Settings.isLatestLWJGL()) {
			return latestLWJGLURL;
		}
		return baseURL;
	}

	public void getNatives() throws IOException, UnsupportedOSException {
		String fileName;

		switch (Utils.getOperatingSystem()) {
			case LINUX:
				fileName = "linux_natives";
				break;
			case MAC_OS:
				fileName = "macosx_natives";
				break;
			case SOLARIS:
				fileName = "solaris_natives";
				break;
			case WINDOWS:
				fileName = "windows_natives";
				break;
			case UNKNOWN:
			default:
				throw new UnsupportedOSException();
		}

		// Download Natives \\
		YAMLNode node = LibrariesYML.getLibrariesYML().getNode(fileName);
		String version = Settings.isLatestLWJGL() ? node.getString("latest") : node.getString("recommended");
		StringBuilder url = new StringBuilder().append("Libraries/").append(fileName).append("/").append(fileName).append("-").append(version).append(".jar");
		String mirrorUrl = MirrorUtils.getMirrorUrl(url.toString(), MirrorUtils.getBaseURL() + url, this);
		File nativesJar = new File(getUpdateDir(), "natives.jar");
		DownloadUtils.downloadFile(mirrorUrl, nativesJar.getPath(), null, node.getNode("versions").getString(version), this);

		// Extract Natives \\
		List<String> ignores = new ArrayList<String>();
		ignores.add("META-INF");
		File tempNatives = new File(getUpdateDir(), "natives");
		Utils.extractJar(new JarFile(nativesJar), tempNatives, ignores);
		FileUtils.moveDirectory(tempNatives, new File(getBinDir(), "natives"));
	}

	public void updateSpoutcraft() throws Exception {
		//performBackup();
		SpoutcraftBuild build = SpoutcraftBuild.getSpoutcraftBuild();
		cleanupBinFoldersFor(build);

		getUpdateDir().mkdirs();
		getBinCacheDir().mkdirs();
		getSpoutcraftDir().mkdirs();
		File cacheDir = new File(getBinDir(), "cache");
		cacheDir.mkdir();

		File mcCache = new File(getBinCacheDir(), "minecraft_" + build.getMinecraftVersion() + ".jar");
		File updateMC = new File(getUpdateDir().getPath() + File.separator + "minecraft.jar");
		if (mcCache.exists()) {
			Utils.copy(mcCache, updateMC);
		}

		File spoutcraft = new File(getBinDir(), "spoutcraft.jar");
		if (spoutcraft.exists() && build.getInstalledBuild() > 0) {
			//Save our installed copy
			File spoutcraftCache = new File(cacheDir, "spoutcraft_" + build.getInstalledBuild() + ".jar");
			if (!spoutcraftCache.exists()) {
				Utils.copy(spoutcraft, spoutcraftCache);
			}
			spoutcraft.delete();
			//Check for an old copy of this build if it is already saved
			spoutcraftCache = new File(cacheDir, "spoutcraft_" + build.getBuild() + ".jar");
			if (spoutcraftCache.exists()) {
				Utils.copy(spoutcraftCache, spoutcraft);
			}
		}

		stateChanged("Looking Up Mirrors...", 0F);
		build.setDownloadListener(this);

		String url = build.getSpoutcraftURL();

		if (url == null) {
			throw new NoMirrorsAvailableException();
		}

		if (!spoutcraft.exists()) {
			Download download = DownloadUtils.downloadFile(url, getUpdateDir() + File.separator + "spoutcraft.jar", null, build.getMD5(), this);
			if (download.isSuccess()) {
				Utils.copy(download.getOutFile(), spoutcraft);
			}
		}

		File libDir = new File(getBinDir(), "lib");
		libDir.mkdir();

		Map<String, Object> libraries = build.getLibraries();
		Iterator<Entry<String, Object>> i = libraries.entrySet().iterator();
		while (i.hasNext()) {
			Entry<String, Object> lib = i.next();
			String version = String.valueOf(lib.getValue());
			String name = lib.getKey() + "-" + version;

			File libraryFile = new File(libDir, lib.getKey() + ".jar");
			String MD5 = LibrariesYML.getMD5(lib.getKey(), version);

			if (libraryFile.exists()) {
				String computedMD5 = MD5Utils.getMD5(libraryFile);
				if (!computedMD5.equals(MD5)) {
					libraryFile.delete();
				}
			}

			if (!libraryFile.exists()) {
				String mirrorURL = "/Libraries/" + lib.getKey() + "/" + name + ".jar";
				String fallbackURL = "http://dl.getspout.org/Libraries/" + lib.getKey() + "/" + name + ".jar";
				url = MirrorUtils.getMirrorUrl(mirrorURL, fallbackURL, this);
				DownloadUtils.downloadFile(url, libraryFile.getPath(), null, MD5, this);
			}
		}

		build.install();
	}

	public void cleanupBinFoldersFor(SpoutcraftBuild build) {
		try {
			if (!getBinDir().exists()) {
				return;
			}

			HashSet<String> neededBinFiles = new HashSet<String>(Arrays.asList(new String[]{"spoutcraft.jar", "minecraft.jar", "lwjgl.jar", "lwjgl_util.jar", "jinput.jar"}));
			for (File file : getBinDir().listFiles()) {
				if (!file.isFile()) {
					continue;
				}
				if (neededBinFiles.contains(file.getName())) {
					continue;
				}
				file.delete();
			}

			File libDir = new File(getBinDir(), "lib");
			if (libDir.exists()) {
				for (File file : libDir.listFiles()) {
					if (!file.isFile()) {
						continue;
					}
					if (build.getLibraries().containsKey(Utils.getFileExtention(file.getName()))) {
						continue;
					}
					file.delete();
				}
			}
		} catch (Exception e) {
			System.out.println("Error while cleaning unnecessary junk... :c");
			e.printStackTrace();
		}
	}

	@Override
	public void clearVersionsInYMLs() {
		SpoutcraftYML.getSpoutcraftYML().setProperty("current", "");
		MinecraftYML.setInstalledVersion("");
	}

	@Override
	public void setStartValidationTime(long validationTime) {
		this.validationTime = validationTime;
	}

	@Override
	public void runValidator() {
		System.out.println("------------------ Validating Install ------------------");
		InstallValidationWorker validator = new InstallValidationWorker(this);
		validationTime = System.currentTimeMillis();
		validator.execute();
	}

	protected void validationFinished(boolean result) {
		long end = System.currentTimeMillis();
		System.out.println("------------------ Validation Finished  ------------------");
		System.out.println("Finished in " + (end - validationTime) +  "ms");
		System.out.println("Result: " + result);

		if (result) {
			Launcher.getSkinManager().getEnabledSkin().getLoginFrame().onRawEvent(Event.VALIDATION_PASSED);
		} else {
			Launcher.getSkinManager().getEnabledSkin().getLoginFrame().onRawEvent(Event.VALIDATION_FAILED);
		}
	}
}
