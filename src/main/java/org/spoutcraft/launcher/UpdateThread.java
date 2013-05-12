/*
 * This file is part of Spoutcraft Launcher.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spoutcraft Launcher is licensed under the Spout License Version 1.
 *
 * Spoutcraft Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spoutcraft Launcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license,
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
import org.spoutcraft.launcher.exceptions.RestfulAPIException;
import org.spoutcraft.launcher.exceptions.UnsupportedOSException;
import org.spoutcraft.launcher.launch.MinecraftClassLoader;
import org.spoutcraft.launcher.launch.MinecraftLauncher;
import org.spoutcraft.launcher.rest.Library;
import org.spoutcraft.launcher.rest.Minecraft;
import org.spoutcraft.launcher.rest.RestAPI;
import org.spoutcraft.launcher.rest.Versions;
import org.spoutcraft.launcher.util.Download;
import org.spoutcraft.launcher.util.DownloadListener;
import org.spoutcraft.launcher.util.DownloadUtils;
import org.spoutcraft.launcher.util.FileUtils;
import org.spoutcraft.launcher.util.MD5Utils;
import org.spoutcraft.launcher.util.OperatingSystem;
import org.spoutcraft.launcher.util.Utils;
import org.spoutcraft.launcher.util.Download.Result;
import org.spoutcraft.launcher.yml.Resources;
import org.spoutcraft.launcher.yml.YAMLProcessor;

public class UpdateThread extends Thread {
	/**
	 * We only want to clean the old logs, temp folders once per startup
	 */
	private static final AtomicBoolean cleaned = new AtomicBoolean(false);
	private static final int PRELOAD_CLASSES = 100;
	public static final String[] OLD_ASSETS = { "1.2.3", "1.2.5", "1.3.1", "1.3.2", "1.4.2", "1.4.4", "1.4.5", "1.4.6", "1.4.7", "1.5" };

	private final Logger logger = Logger.getLogger("launcher");
	private final AtomicBoolean waiting = new AtomicBoolean(false);
	private final AtomicBoolean valid = new AtomicBoolean(false);
	private final AtomicBoolean finished = new AtomicBoolean(false);
	private final StartupParameters params = Utils.getStartupParameters();
	private final DownloadListener listener = new DownloadListenerWrapper();
	private volatile SpoutcraftData build;
	private final Thread previous;
	public UpdateThread() {
		this(null);
	}

	public UpdateThread(Thread previous) {
		super("Update Thread");
		setDaemon(true);
		this.previous = previous;
	}

	public SpoutcraftData getBuild() {
		return build;
	}

	@Override
	public void run() {
		try {
			build = new SpoutcraftData();
			boolean interrupted = false;
			while(true) {
				try {
					if (previous != null) {
						previous.join();
					}
					break;
				} catch (InterruptedException e) {
					interrupted = true;
				}
			}
			if (!interrupted) {
				runTasks();
			}
		} catch (Exception e) {
			if (!this.isInterrupted()) {
				Launcher.getLoginFrame().handleException(e);
			}
		}
	}

	private void runTasks() throws IOException {
		while (!valid.get()) {
			boolean minecraftUpdate = isMinecraftUpdateAvailable(build);
			boolean spoutcraftUpdate = minecraftUpdate || isSpoutcraftUpdateAvailable(build);

			if (minecraftUpdate) {
				updateMinecraft(build);
			}
			if (spoutcraftUpdate) {
				updateSpoutcraft(build);
			}

			updateAssets();

			// Download assets
			if (cleaned.compareAndSet(false, true)) {
				Resources.VIP.getYAML();
				Resources.Special.getYAML();
				Versions.getMinecraftVersions();

				cleanLogs();
				cleanTemp();
				updateFiles();

				RestAPI.getCache().cleanup();
			}

			Validator validate = new Validator();
			if (!(params.isIgnoreMD5() || Settings.isIgnoreMD5())) {
				validate.run(build);
				valid.set(validate.isValid());
			} else {
				valid.set(true);
			}
		}

		MinecraftClassLoader loader;
		loader = MinecraftLauncher.getClassLoader(build.getLibraries());

		int loaded = 0;
		while (!waiting.get()) {
			int pass = loader.preloadClasses(PRELOAD_CLASSES);
			loaded += pass;
			// Less than the preload amount, so we are finished
			if (pass != PRELOAD_CLASSES) {
				break;
			}
		}
		logger.info("Preloaded " + loaded + " classes in advance");
		finished.set(true);
	}

	private void updateAssets() {
		YAMLProcessor assets = Resources.Assets.getYAML();
		if (assets != null) {
			updateAssets(assets.getMap(), Utils.getAssetsDirectory());
		}
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
					Launcher.debug("Checking MD5 of " + asset.getName() + ". Expected MD5: " + key + " | Actual MD5: " + md5);
					needDownload = md5 == null || !md5.equals(key);
				} else if (asset.exists() && (params.isIgnoreMD5() || Settings.isIgnoreMD5())) {
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
			if (f.isDirectory() && f.getName().startsWith("temp_")) {
				FileUtils.deleteQuietly(f);
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

	public boolean isSpoutcraftUpdateAvailable(SpoutcraftData build) throws RestfulAPIException {
		if (!Utils.getWorkingDirectory().exists()) {
			return true;
		}
		if (!Launcher.getGameUpdater().getSpoutcraftDir().exists()) {
			return true;
		}

		List<Library> libraries = build.getLibraries();
		int steps = libraries.size() + 2;
		float progress = 100F;

		stateChanged("Checking for Spoutcraft update...", progress / steps);
		progress += 100F;
		File spoutcraft = new File(Launcher.getGameUpdater().getBinDir(), "spoutcraft.jar");
		if (!spoutcraft.exists()) {
			return true;
		}
		if (!Settings.isIgnoreMD5() && !build.getMD5().equalsIgnoreCase(MD5Utils.getMD5(spoutcraft))) {
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

	public boolean isMinecraftUpdateAvailable(SpoutcraftData build) {
		int steps = 7;
		if (!Launcher.getGameUpdater().getBinDir().exists()) {
			return true;
		}
		stateChanged("Checking for Minecraft update...", 100F / steps);
		File nativesDir = new File(Launcher.getGameUpdater().getBinDir(), "natives");
		if (!nativesDir.exists()) {
			return true;
		}
		// Empty directory
		if (nativesDir.listFiles().length == 0) {
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
		String installed = Settings.getInstalledMC();
		stateChanged("Checking for Minecraft update...", 700F / steps);
		String required = build.getMinecraft().getVersion();
		return installed == null || !installed.equals(required);
	}

	// Searches the Minecraft libraries for the one matching the given artifact ID, returns the MD5
	protected static String findMd5(String artifactId, OperatingSystem platform, List<Library> libraries) {
		for (Library lib : libraries) {
			if (lib.getArtifactId().equalsIgnoreCase(artifactId)) {
				if (platform == null
					|| platform.isWindows() && lib.getVersion().toLowerCase().endsWith("windows")
					|| platform.isMac() && lib.getVersion().toLowerCase().endsWith("osx")
					|| platform.isUnix() && lib.getVersion().toLowerCase().endsWith("linux")) {
					return lib.getMd5();
				}
			}
		}
		// Should never get here...
		throw new IllegalArgumentException("No valid library for " + artifactId + ", with platform " + platform + " was found");
	}

	public void updateMinecraft(SpoutcraftData build) throws IOException {
		Launcher.getGameUpdater().getBinDir().mkdir();
		Launcher.getGameUpdater().getBinCacheDir().mkdir();
		if (Launcher.getGameUpdater().getUpdateDir().exists()) {
			FileUtils.deleteDirectory(Launcher.getGameUpdater().getUpdateDir());
		}
		Launcher.getGameUpdater().getUpdateDir().mkdir();

		final Minecraft minecraft = build.getMinecraft();
		final String minecraftMD5 = minecraft.getMd5();
		final String jinputMD5 = findMd5("jinput", null, minecraft.getLibraries());
		final String lwjgl_utilMD5 = findMd5("lwjgl_util", null, minecraft.getLibraries());
		final String lwjglMD5 = findMd5("lwjgl", null, minecraft.getLibraries());

		// Process minecraft.jar
		logger.info("Minecraft Version: " + build.getMinecraft());
		File mcCache = new File(Launcher.getGameUpdater().getBinCacheDir(), "minecraft_" + minecraft.getVersion() + ".jar");
		if (!mcCache.exists() || (minecraftMD5 == null || !minecraftMD5.equals(MD5Utils.getMD5(mcCache)))) {
			if (Arrays.asList(OLD_ASSETS).contains(minecraft.getVersion())) {
				DownloadUtils.downloadFile("http://assets.minecraft.net/" + minecraft.getVersion().replaceAll("\\.", "_") + "/minecraft.jar", mcCache.getPath(), null, minecraftMD5, listener);
			} else {
				DownloadUtils.downloadFile("http://s3.amazonaws.com/Minecraft.Download/versions/" + minecraft.getVersion() + "/" + minecraft.getVersion() + ".jar", mcCache.getPath(), null, minecraftMD5, listener);
			}
		}
		Utils.copy(mcCache, new File(Launcher.getGameUpdater().getBinDir(), "minecraft.jar"));

		File nativesDir = new File(Launcher.getGameUpdater().getBinDir(), "natives");
		nativesDir.mkdir();

		stateChanged("Extracting Files...", 0);

		Settings.setInstalledMC(build.getMinecraft().getVersion());
		Settings.getYAML().save();
	}

	public void updateSpoutcraft(SpoutcraftData build) throws IOException {
		cleanupBinFoldersFor(build);

		Launcher.getGameUpdater().getUpdateDir().mkdirs();
		Launcher.getGameUpdater().getBinCacheDir().mkdirs();
		Launcher.getGameUpdater().getSpoutcraftDir().mkdirs();
		File cacheDir = new File(Launcher.getGameUpdater().getBinDir(), "cache");
		cacheDir.mkdir();

		// Process spoutcraft.jar
		logger.info("Spoutcraft Build: " + build.getBuild());
		File mcCache = new File(Launcher.getGameUpdater().getBinCacheDir(), "minecraft_" + build.getMinecraft().getVersion() + ".jar");
		File updateMC = new File(Launcher.getGameUpdater().getUpdateDir().getPath() + File.separator + "minecraft.jar");
		if (mcCache.exists()) {
			Utils.copy(mcCache, updateMC);
		}

		File spoutcraft = new File(Launcher.getGameUpdater().getBinDir(), "spoutcraft.jar");
		if (spoutcraft.exists()) {
			File spoutcraftCache;
			if (Integer.parseInt(build.getInstalledBuild()) > 0) {
				// Save our installed copy
				spoutcraftCache = new File(cacheDir, "spoutcraft_" + build.getInstalledBuild() + ".jar");
				if (!spoutcraftCache.exists()) {
					Utils.copy(spoutcraft, spoutcraftCache);
				}
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

		List<Library> allLibraries = new ArrayList<Library>();
		allLibraries.addAll(build.getLibraries());
		allLibraries.addAll(build.getMinecraft().getLibraries());
		for (Library lib : allLibraries) {
			File libraryFile = new File(libDir, lib.name() + ".jar");
			if (lib.getArtifactId().contains("lwjgl") || lib.getArtifactId().contains("jinput")) {

				if (lib.getVersion().contains("natives")) {
					File nativesFolder = new File(Launcher.getGameUpdater().getBinDir(), "natives");
					//This prevents downloading the wrong natives jar
					if (lib.getVersion().contains("osx") && !OperatingSystem.getOS().isMac()) {
						continue;
					} else if (lib.getVersion().contains("linux") && !OperatingSystem.getOS().isUnix()) {
						continue;
					} else if (lib.getVersion().contains("windows") && !OperatingSystem.getOS().isWindows()) {
						continue;
					}

					// Download natives

					File nativesJar = new File(Launcher.getGameUpdater().getUpdateDir(), "natives.jar");
					if (!nativesJar.exists() || !lib.valid(MD5Utils.getMD5(nativesJar))) {
						lib.download(nativesJar, listener);

						// Extract natives into the proper directory
						List<String> ignores = new ArrayList<String>();
						ignores.add("META-INF");
						File tempNatives = new File(Launcher.getGameUpdater().getUpdateDir(), "natives");
						Utils.extractJar(new JarFile(nativesJar), tempNatives, ignores);
						FileUtils.moveDirectory(tempNatives, nativesFolder);

					}
					continue;
				}

				libraryFile = new File(Launcher.getGameUpdater().getBinDir(), lib.getArtifactId() + ".jar");
			}

			if (libraryFile.exists()) {
				String computedMD5 = MD5Utils.getMD5(libraryFile);
				if (!lib.valid(computedMD5)) {
					logger.warning("MD5 check of " + libraryFile.getName() + " failed. Deleting and Redownloading.");
					libraryFile.delete();
				}
			}
			File cachedLibraryFile = new File(cacheDir, lib.name() + ".jar");
			if (cachedLibraryFile.exists()) {
				String computedMD5 = MD5Utils.getMD5(cachedLibraryFile);
				if (lib.valid(computedMD5)) {
					Utils.copy(cachedLibraryFile, libraryFile);
				}
			}
			if (!libraryFile.exists()) {
				lib.download(libraryFile, listener);
			}
		}
	}

	public void cleanupBinFoldersFor(SpoutcraftData build) {
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
