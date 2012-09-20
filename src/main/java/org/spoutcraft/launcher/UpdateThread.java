/*
 * This file is part of Spoutcraft Launcher.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spoutcraft Launcher is licensed under the SpoutDev License Version 1.
 *
 * Spoutcraft Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spoutcraft Launcher is distributed in the hope that it will be useful,
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
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.spoutcraft.launcher.api.Launcher;
import org.spoutcraft.launcher.api.SpoutcraftDirectories;
import org.spoutcraft.launcher.exceptions.UnsupportedOSException;
import org.spoutcraft.launcher.launch.MinecraftClassLoader;
import org.spoutcraft.launcher.launch.MinecraftLauncher;
import org.spoutcraft.launcher.rest.Library;
import org.spoutcraft.launcher.rest.Versions;
import org.spoutcraft.launcher.rest.exceptions.RestfulAPIException;
import org.spoutcraft.launcher.util.Download;
import org.spoutcraft.launcher.util.DownloadListener;
import org.spoutcraft.launcher.util.DownloadUtils;
import org.spoutcraft.launcher.util.FileType;
import org.spoutcraft.launcher.util.FileUtils;
import org.spoutcraft.launcher.util.MD5Utils;
import org.spoutcraft.launcher.util.MinecraftDownloadUtils;
import org.spoutcraft.launcher.util.MirrorUtils;
import org.spoutcraft.launcher.util.OperatingSystem;
import org.spoutcraft.launcher.util.Utils;
import org.spoutcraft.launcher.util.Download.Result;
import org.spoutcraft.launcher.yml.Resources;
import org.spoutcraft.launcher.yml.SpoutcraftBuild;
import org.spoutcraft.launcher.yml.YAMLNode;
import org.spoutcraft.launcher.yml.YAMLProcessor;

public class UpdateThread extends Thread {
	/**
	 * We only want to clean the old logs, temp folders once per startup
	 */
	private static final AtomicBoolean cleaned = new AtomicBoolean(false);
	private final Logger logger = Logger.getLogger("launcher");
	private final AtomicBoolean waiting = new AtomicBoolean(false);
	private final AtomicBoolean valid = new AtomicBoolean(false);
	private final AtomicBoolean finished = new AtomicBoolean(false);
	private final StartupParameters params = Utils.getStartupParameters();
	private final DownloadListener listener = new DownloadListenerWrapper();
	public UpdateThread(DownloadListener listener) {
		super("Update Thread");
		setDownloadListener(listener);
	}

	@Override
	public void run() {
		while (true) {
			try {
				runTasks();
				break;
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Unable to execute tasks", e);
			}
			try {
				sleep(1000);
			} catch (InterruptedException ignore) { }
		}
	}

	private void runTasks() throws RestfulAPIException {
		while (!valid.get()) {
			boolean minecraftUpdate = isMinecraftUpdateAvailable();
			boolean spoutcraftUpdate = minecraftUpdate || isSpoutcraftUpdateAvailable();

			if (minecraftUpdate) {
				try {
					updateMinecraft();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (spoutcraftUpdate) {
				try {
					updateSpoutcraft();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			updateAssets();
			
			//Download assets
			if (cleaned.compareAndSet(false, true)) {
				Resources.VIP.getYAML();
				Resources.Special.getYAML();
				Versions.getMinecraftVersions();
				
				cleanLogs();
				cleanTemp();
				updateFiles();
			}

			Validator validate = new Validator();
			if (!(params.isIgnoreMD5() || Settings.isIgnoreMD5())) {
				validate.run();
				valid.set(validate.isValid());
			} else {
				valid.set(true);
			}
		}

		if (valid.get()) {
			MinecraftClassLoader loader;
			try {
				loader = MinecraftLauncher.getClassLoader();
				
				int loaded = 0;
				final int PRELOAD_PASS = 250;
				while (true) {
					if (waiting.get()) {
						break;
					}
					int pass = loader.preloadClasses(PRELOAD_PASS);
					loaded += pass;
					// Less than the preload amount, so we are finished
					if (pass != PRELOAD_PASS) {
						break;
					}
				}
				logger.info("Preloaded " + loaded + " classes in advance");
			} catch (RestfulAPIException e) {
				e.printStackTrace();
			}
		} else {
			try {
				sleep(100);
			}
			catch (InterruptedException ignore) { }
		}

		finished.set(true);
	}

	private void updateAssets() {
		YAMLProcessor assets = Resources.Assets.getYAML();
		updateAssets(assets.getMap(), Utils.getAssetsDirectory());
	}

	@SuppressWarnings("unchecked")
	private void updateAssets(Map<String, Object> assets, File directory) {
		directory.mkdirs();
		for (Entry<String, Object> entry : assets.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (value instanceof Map<?, ?>) {
				updateAssets((Map<String, Object>)value, new File(directory, key));
			} else if (value instanceof String) {
				String url = (String)value;
				String name = getFileName(url);
				File asset = new File(directory, name);

				stateChanged("Verifying Asset: " + name, 0);

				boolean needDownload = true;
				if (asset.exists() && !(params.isIgnoreMD5() || Settings.isIgnoreMD5())) {
					String md5 = MD5Utils.getMD5(asset);
					logger.info("Checking MD5 of " + asset.getName() + ". Expected MD5: " + key + " | Actual MD5: " + md5);
					needDownload = md5 == null || !md5.equals(key);
				} else if (asset.exists() && params.isIgnoreMD5()) {
					needDownload = false;
				}

				if (needDownload) {
					try {
						DownloadUtils.downloadFile(url, asset.getPath(), null, key, listener);
					} catch (IOException e) {
						logger.log(Level.SEVERE, "Failed to download asset [" + url + "]", e);
					}
				}
				stateChanged("Verified Asset: " + name, 100);
			} else {
				logger.warning("Unknown asset type for " + key + ". Type is " + value);
			}
		}
	}

	private String getFileName(String url) {
		String[] split = url.split("/");
		return split[split.length - 1];
	}

	private void updateFiles() {
		SpoutcraftDirectories dirs = new SpoutcraftDirectories();
		File oldConfig = new File(Utils.getWorkingDirectory(), "spoutcraft");
		if (oldConfig.exists() && oldConfig.isDirectory()) {
			moveDirectory(oldConfig, dirs.getSpoutcraftDir());
			FileUtils.deleteQuietly(oldConfig);
		}
	}

	private void moveDirectory(File dir, File newDir) {
		if (!dir.exists()) {
			dir.mkdirs();
		}
		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				moveDirectory(file, new File(newDir, file.getName()));
			} else {
				file.renameTo(new File(newDir, file.getName()));
			}
		}
	}

	private void cleanTemp() {
		SpoutcraftDirectories dirs = new SpoutcraftDirectories();
		File binDir = dirs.getBinDir();
		for (File f : binDir.listFiles()) {
			if (f.isDirectory()) {
				if (f.getName().startsWith("temp_")) {
					FileUtils.deleteQuietly(f);
				}
			}
		}
	}

	private void cleanLogs() {
		File logDirectory = new File(Utils.getWorkingDirectory(), "logs");
		if (logDirectory.exists() && logDirectory.isDirectory()) {
			for (File log : logDirectory.listFiles()) {
				if (!log.getName().endsWith(".log")) {
					log.delete();
					continue;
				}

				if (!log.getName().startsWith("spoutcraft")) {
					log.delete();
					continue;
				}

				String[] split = log.getName().split("_");
				if (split.length != 2) {
					log.delete();
					continue;
				}

				String[] date = split[1].split("-");
				if (date.length != 3) {
					log.delete();
					continue;
				}
				date[2] = date[2].substring(0, date[2].length() - 4); // Trim .log extension
				try {
					int logYear = Integer.parseInt(date[0]);
					int logMonth = Integer.parseInt(date[1]);
					int logDay = Integer.parseInt(date[2]);

					Calendar logDate = Calendar.getInstance();
					// Add a month to the calendar (clear logs older than 1 month)
					if (logMonth < 12) {
						logMonth++;
					} else {
						logMonth = 1;
						logYear++;
					}
					logDate.set(logYear, logMonth, logDay);

					if (Calendar.getInstance().after(logDate)) {
						log.delete();
					}
				} catch (NumberFormatException ignore) {
					log.delete();
					continue;
				}
			}
		}
	}

	public void setWaiting(boolean waiting) {
		this.waiting.set(waiting);
	}

	public boolean isFinished() {
		return finished.get();
	}

	public boolean isValidInstall() {
		return valid.get();
	}

	public boolean isSpoutcraftUpdateAvailable() throws RestfulAPIException {
		if (!Utils.getWorkingDirectory().exists()) {
			return true;
		}
		if (!Launcher.getGameUpdater().getSpoutcraftDir().exists()) {
			return true;
		}

		SpoutcraftBuild build = SpoutcraftBuild.getSpoutcraftBuild();
		List<Library> libraries = build.getLibraries();
		int steps = libraries.size() + 2;
		float progress = 100F;

		if (build.getBuild() != build.getInstalledBuild()) {
			return true;
		}
		stateChanged("Checking for Spoutcraft update...", progress / steps);
		progress += 100F;
		File spoutcraft = new File(Launcher.getGameUpdater().getBinDir(), "spoutcraft.jar");
		if (!spoutcraft.exists()) {
			return true;
		}
		stateChanged("Checking for Spoutcraft update...", progress / steps);
		progress += 100F;
		File libDir = new File(Launcher.getGameUpdater().getBinDir(), "lib");
		libDir.mkdir();

		for (Library lib : libraries) {
			File libraryFile = new File(libDir, lib.name() + ".jar");
			if (!libraryFile.exists()) {
				return true;
			}
			stateChanged("Checking for Spoutcraft update...", progress / steps);
			progress += 100F;
		}

		return false;
	}

	public boolean isMinecraftUpdateAvailable() {
		int steps = 7;
		if (!Launcher.getGameUpdater().getBinDir().exists()) {
			return true;
		}
		stateChanged("Checking for Minecraft update...", 100F / steps);
		if (!new File(Launcher.getGameUpdater().getBinDir(), "natives").exists()) {
			return true;
		}
		stateChanged("Checking for Minecraft update...", 200F / steps);
		File minecraft = new File(Launcher.getGameUpdater().getBinDir(), "minecraft.jar");
		if (!minecraft.exists()) {
			return true;
		}
		stateChanged("Checking for Minecraft update...", 300F / steps);
		File lib = new File(Launcher.getGameUpdater().getBinDir(), "jinput.jar");
		if (!lib.exists()) {
			return true;
		}
		stateChanged("Checking for Minecraft update...", 400F / steps);
		lib = new File(Launcher.getGameUpdater().getBinDir(), "lwjgl.jar");
		if (!lib.exists()) {
			return true;
		}
		stateChanged("Checking for Minecraft update...", 500F / steps);
		lib = new File(Launcher.getGameUpdater().getBinDir(), "lwjgl_util.jar");
		if (!lib.exists()) {
			return true;
		}
		stateChanged("Checking for Minecraft update...", 600F / steps);
		SpoutcraftBuild build = SpoutcraftBuild.getSpoutcraftBuild();
		String installed = Resources.getInstalledVersion();
		stateChanged("Checking for Minecraft update...", 700F / steps);
		String required = build.getMinecraftVersion();
		return installed == null || !installed.equals(required);
	}

	public void updateMinecraft() throws IOException {
		Launcher.getGameUpdater().getBinDir().mkdir();
		Launcher.getGameUpdater().getBinCacheDir().mkdir();
		if (Launcher.getGameUpdater().getUpdateDir().exists()) {
			FileUtils.deleteDirectory(Launcher.getGameUpdater().getUpdateDir());
		}
		Launcher.getGameUpdater().getUpdateDir().mkdir();

		String minecraftMD5 = MD5Utils.getMD5(FileType.MINECRAFT);
		String jinputMD5 = MD5Utils.getMD5(FileType.JINPUT);
		String lwjglMD5 = MD5Utils.getMD5(FileType.LWJGL);
		String lwjgl_utilMD5 = MD5Utils.getMD5(FileType.LWJGL);

		SpoutcraftBuild build = SpoutcraftBuild.getSpoutcraftBuild();

		// Processs minecraft.jar
		logger.info("Spoutcraft Build: " + build.getBuild() + " Minecraft Version: " + build.getMinecraftVersion());
		File mcCache = new File(Launcher.getGameUpdater().getBinCacheDir(), "minecraft_" + build.getMinecraftVersion() + ".jar");
		if (!mcCache.exists() || (minecraftMD5 == null || !minecraftMD5.equals(MD5Utils.getMD5(mcCache)))) {
			String output = Launcher.getGameUpdater().getUpdateDir() + File.separator + "minecraft.jar";
			MinecraftDownloadUtils.downloadMinecraft(Launcher.getGameUpdater().getMinecraftUser(), output, build, listener);
		}
		Utils.copy(mcCache, new File(Launcher.getGameUpdater().getBinDir(), "minecraft.jar"));

		File nativesDir = new File(Launcher.getGameUpdater().getBinDir().getPath(), "natives");
		nativesDir.mkdir();

		// Process other downloads
		mcCache = new File(Launcher.getGameUpdater().getBinCacheDir(), "jinput.jar");
		if (!mcCache.exists() || !jinputMD5.equals(MD5Utils.getMD5(mcCache))) {
			DownloadUtils.downloadFile(getNativesUrl() + "jinput.jar", Launcher.getGameUpdater().getBinDir().getPath() + File.separator + "jinput.jar", "jinput.jar");
		} else {
			Utils.copy(mcCache, new File(Launcher.getGameUpdater().getBinDir(), "jinput.jar"));
		}


		mcCache = new File(Launcher.getGameUpdater().getBinCacheDir(), "lwjgl.jar");
		if (!mcCache.exists() || !lwjglMD5.equals(MD5Utils.getMD5(mcCache))) {
			DownloadUtils.downloadFile(getNativesUrl() + "lwjgl.jar", Launcher.getGameUpdater().getBinDir().getPath() + File.separator + "lwjgl.jar", "lwjgl.jar");
		} else {
			Utils.copy(mcCache, new File(Launcher.getGameUpdater().getBinDir(), "lwjgl.jar"));
		}

		mcCache = new File(Launcher.getGameUpdater().getBinCacheDir(), "lwjgl_util.jar");
		if (!mcCache.exists() || !lwjgl_utilMD5.equals(MD5Utils.getMD5(mcCache))) {
			DownloadUtils.downloadFile(getNativesUrl() + "lwjgl_util.jar", Launcher.getGameUpdater().getBinDir().getPath() + File.separator + "lwjgl_util.jar", "lwjgl_util.jar");
		} else {
			Utils.copy(mcCache, new File(Launcher.getGameUpdater().getBinDir(), "lwjgl_util.jar"));
		}

		try {
			getNatives();
		} catch (Exception e) {
			e.printStackTrace();
		}

		stateChanged("Extracting Files...", 0);

		Resources.setInstalledVersion(build.getMinecraftVersion());
	}

	public String getNativesUrl() {
		return Launcher.getGameUpdater().baseURL;
	}

	public void getNatives() throws IOException, UnsupportedOSException {
		String fileName;

		OperatingSystem os = OperatingSystem.getOS();
		if (os.isUnix()) {
			fileName = "linux_natives";
		} else if (os.isMac()) {
			fileName = "macosx_natives";
		} else if (os.isWindows()) {
			fileName = "windows_natives";
		} else {
			throw new UnsupportedOperationException("Unknown OS: " + os);
		}

		// Download natives
		YAMLNode node = Resources.Libraries.getYAML().getNode(fileName);
		String version = node.getString("recommended");
		StringBuilder url = new StringBuilder().append("lib/").append(fileName).append("/").append(fileName).append("-").append(version).append(".jar");
		String mirrorUrl = MirrorUtils.getMirrorUrl(url.toString(), MirrorUtils.getBaseURL() + url);
		File nativesJar = new File(Launcher.getGameUpdater().getUpdateDir(), "natives.jar");
		DownloadUtils.downloadFile(mirrorUrl, nativesJar.getPath(), null, node.getNode("versions").getString(version), listener);

		// Extract natives
		List<String> ignores = new ArrayList<String>();
		ignores.add("META-INF");
		File tempNatives = new File(Launcher.getGameUpdater().getUpdateDir(), "natives");
		Utils.extractJar(new JarFile(nativesJar), tempNatives, ignores);
		FileUtils.moveDirectory(tempNatives, new File(Launcher.getGameUpdater().getBinDir(), "natives"));
	}

	public void updateSpoutcraft() throws Exception {
		//performBackup();
		SpoutcraftBuild build = SpoutcraftBuild.getSpoutcraftBuild();
		cleanupBinFoldersFor(build);

		Launcher.getGameUpdater().getUpdateDir().mkdirs();
		Launcher.getGameUpdater().getBinCacheDir().mkdirs();
		Launcher.getGameUpdater().getSpoutcraftDir().mkdirs();
		File cacheDir = new File(Launcher.getGameUpdater().getBinDir(), "cache");
		cacheDir.mkdir();

		File mcCache = new File(Launcher.getGameUpdater().getBinCacheDir(), "minecraft_" + build.getMinecraftVersion() + ".jar");
		File updateMC = new File(Launcher.getGameUpdater().getUpdateDir().getPath() + File.separator + "minecraft.jar");
		if (mcCache.exists()) {
			Utils.copy(mcCache, updateMC);
		}

		File spoutcraft = new File(Launcher.getGameUpdater().getBinDir(), "spoutcraft.jar");
		if (spoutcraft.exists() && build.getInstalledBuild() > 0) {
			// Save our installed copy
			File spoutcraftCache = new File(cacheDir, "spoutcraft_" + build.getInstalledBuild() + ".jar");
			if (!spoutcraftCache.exists()) {
				Utils.copy(spoutcraft, spoutcraftCache);
			}
			spoutcraft.delete();
			// Check for an old copy of this build if it is already saved
			spoutcraftCache = new File(cacheDir, "spoutcraft_" + build.getBuild() + ".jar");
			if (spoutcraftCache.exists()) {
				Utils.copy(spoutcraftCache, spoutcraft);
			}
		}

		stateChanged("Looking Up Mirrors...", 0F);

		String url = build.getSpoutcraftURL();

		if (!spoutcraft.exists()) {
			Download download = DownloadUtils.downloadFile(url, Launcher.getGameUpdater().getUpdateDir() + File.separator + "spoutcraft.jar", null, build.getMD5(), listener);
			if (download.getResult() == Result.SUCCESS) {
				Utils.copy(download.getOutFile(), spoutcraft);
			}
		}

		File libDir = new File(Launcher.getGameUpdater().getBinDir(), "lib");
		libDir.mkdir();

		List<Library> libraries = build.getLibraries();
		for (Library lib : libraries) {
			String name = lib.name();
			File libraryFile = new File(libDir, lib.name() + ".jar");
			if (libraryFile.exists()) {
				String computedMD5 = MD5Utils.getMD5(libraryFile);
				if (lib.valid(computedMD5)) {
					logger.warning("MD5 check of " + libraryFile.getName() + " failed. Deleting and Redownloading.");
					libraryFile.delete();
				}
			}

			if (!libraryFile.exists()) {
				lib.download(libraryFile, listener);
			}
		}

		build.install();
	}

	public void cleanupBinFoldersFor(SpoutcraftBuild build) {
		try {
			if (!Launcher.getGameUpdater().getBinDir().exists()) {
				return;
			}

			HashSet<String> neededBinFiles = new HashSet<String>(Arrays.asList(new String[]{"spoutcraft.jar", "minecraft.jar", "lwjgl.jar", "lwjgl_util.jar", "jinput.jar"}));
			for (File file : Launcher.getGameUpdater().getBinDir().listFiles()) {
				if (!file.isFile()) {
					continue;
				}
				if (neededBinFiles.contains(file.getName())) {
					continue;
				}
				file.delete();
			}
		} catch (Exception e) {
			System.err.println("Error while cleaning files: ");
			e.printStackTrace();
		}
	}

	public DownloadListener getDownloadListener() {
		return listener;
	}

	public void setDownloadListener(DownloadListener listener) {
		((DownloadListenerWrapper)this.listener).setDownloadListener(listener);
	}

	public void stateChanged(String message, float progress) {
		if (listener != null) {
			listener.stateChanged(message, progress);
		}
	}

	private class DownloadListenerWrapper implements DownloadListener {
		private final AtomicReference<DownloadListener> wrapped = new AtomicReference<DownloadListener>(null);
		public void stateChanged(String fileName, float progress) {
			fileName = (new File(fileName)).getName();
			DownloadListener listener = wrapped.get();
			if (listener != null) {
				listener.stateChanged(fileName, progress);
			}
		}

		public void setDownloadListener(DownloadListener listener) {
			wrapped.set(listener);
		}
	}
}
