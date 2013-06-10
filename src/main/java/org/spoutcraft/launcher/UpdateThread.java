/*
 * This file is part of Technic Launcher.
 *
 * Copyright (c) 2013-2013, Technic <http://www.technicpack.net/>
 * Technic Launcher is licensed under the Spout License Version 1.
 *
 * Technic Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Technic Launcher is distributed in the hope that it will be useful,
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

package org.spoutcraft.launcher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.spoutcraft.launcher.api.Launcher;
import org.spoutcraft.launcher.exceptions.DownloadException;
import org.spoutcraft.launcher.exceptions.RestfulAPIException;
import org.spoutcraft.launcher.exceptions.UnzipException;
import org.spoutcraft.launcher.launch.MinecraftClassLoader;
import org.spoutcraft.launcher.launch.MinecraftLauncher;
import org.spoutcraft.launcher.rest.Mod;
import org.spoutcraft.launcher.rest.Modpack;
import org.spoutcraft.launcher.rest.RestAPI;
import org.spoutcraft.launcher.technic.OfflineInfo;
import org.spoutcraft.launcher.technic.PackInfo;
import org.spoutcraft.launcher.util.DownloadListener;
import org.spoutcraft.launcher.util.DownloadUtils;
import org.spoutcraft.launcher.util.FileUtils;
import org.spoutcraft.launcher.util.MD5Utils;
import org.spoutcraft.launcher.util.MinecraftDownloadUtils;
import org.spoutcraft.launcher.util.OperatingSystem;
import org.spoutcraft.launcher.util.Utils;
import org.spoutcraft.launcher.yml.YAMLFormat;
import org.spoutcraft.launcher.yml.YAMLProcessor;

public class UpdateThread extends Thread {
	/**
	 * We only want to clean the old logs, temp folders once per startup
	 */
	private static final AtomicBoolean cleaned = new AtomicBoolean(false);
	private static final int PRELOAD_CLASSES = 100;

	private final Logger logger = Logger.getLogger("launcher");
	private final AtomicBoolean waiting = new AtomicBoolean(false);
	private final AtomicBoolean valid = new AtomicBoolean(false);
	private final AtomicBoolean finished = new AtomicBoolean(false);
	private final DownloadListener listener = new DownloadListenerWrapper();
	private final Modpack build;
	private final PackInfo pack;

	public UpdateThread(PackInfo pack) throws RestfulAPIException {
		super("Update Thread");
		setDaemon(true);
		this.pack = pack;
		this.build = pack.getModpack();
		if (build == null) {
			JOptionPane.showMessageDialog(Launcher.getFrame(), "Error retrieving information for selected pack: " + pack.getDisplayName(), "Error", JOptionPane.WARNING_MESSAGE);
			throw new RestfulAPIException("Error getting modpack build for " + pack.getName());
		}
		setDownloadListener(Launcher.getFrame());
	}

	@Override
	public void run() {
		while (true) {
			try {
				runTasks();
				break;
			} catch (DownloadException e) {
				JOptionPane.showMessageDialog(Launcher.getFrame(), "Error downloading file for the following pack: " + pack.getDisplayName() + " \n\n" + e.getMessage() + "\n\nPlease consult the modpack author.", "Error", JOptionPane.WARNING_MESSAGE);
				Launcher.getFrame().enableForm();
				Launcher.getGameUpdater().resetUpdateThread();
				e.printStackTrace();
				return;
			} catch (UnzipException e) {
				JOptionPane.showMessageDialog(Launcher.getFrame(), "Error unzipping file for the following pack: " + pack.getDisplayName() + " \n\n" + e.getMessage() + "\n\nPlease consult the modpack author, or try to run the pack again.", "Error", JOptionPane.WARNING_MESSAGE);
				Launcher.getFrame().enableForm();
				Launcher.getGameUpdater().resetUpdateThread();
				e.printStackTrace();
				return;
			} catch (Exception e) {
				Launcher.getFrame().handleException(e);
				return;
			}
		}
	}

	private void runTasks() throws IOException {
		while (!valid.get()) {
			if (!pack.isLoading() && build.getMinecraftVersion() != null && !(pack instanceof OfflineInfo)) {
				boolean minecraftUpdate = isMinecraftUpdateAvailable(build);

				boolean modpackUpdate = minecraftUpdate || isModpackUpdateAvailable(build);
				if (modpackUpdate) {
					File installed = new File(pack.getBinDir(), "installed");
					if(!installed.exists()) {
						if (minecraftUpdate) {
							updateMinecraft(build);
						}
						updateModpack(build);
					}
					else {
						int result = JOptionPane.showConfirmDialog(Launcher.getFrame(), "Would you like to update this pack?", "Update Found", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
						if (result == JOptionPane.YES_OPTION) {
							if (minecraftUpdate) {
								updateMinecraft(build);
							}
							updateModpack(build);
						}
					}
				}
			}
			cleanLogs();

			valid.set(true);
		}

		MinecraftClassLoader loader;
		loader = MinecraftLauncher.getClassLoader(pack);
		
		Utils.pingURL(RestAPI.getRunCountURL(build.getName()));

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

	private void cleanLogs() {
		if (!cleaned.compareAndSet(false, true)) {
			return;
		}
		File logDirectory = new File(Utils.getLauncherDirectory(), "logs");
		if (logDirectory.exists() && logDirectory.isDirectory()) {
			for (File log : logDirectory.listFiles()) {
				if (!log.getName().endsWith(".log")) {
					log.delete();
					continue;
				}

				if (!log.getName().startsWith("techniclauncher")) {
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

	public boolean isModpackUpdateAvailable(Modpack build) throws RestfulAPIException {
		if (!Utils.getLauncherDirectory().exists()) {
			return true;
		}
		if (!pack.getConfigDir().exists()) {
			return true;
		}

		float progress = 100F;
		int steps = 2;
		stateChanged("Checking for " + build.getName() + " update...", progress / steps);
		progress += 100F;

		File installed = new File(pack.getBinDir(), "installed");
		if (!installed.exists()) {
			return true;
		}
		YAMLProcessor yaml = new YAMLProcessor(installed, false, YAMLFormat.EXTENDED);
		try {
			yaml.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (!build.getBuild().equals(yaml.getProperty("build"))) {
			return true;
		}
		stateChanged("No update for " + build.getName() + " found.", progress / steps);
		return false;
	}

	public boolean isMinecraftUpdateAvailable(Modpack build) {
		int steps = 7;
		if (!pack.getBinDir().exists()) {
			return true;
		}
		stateChanged("Checking for Minecraft update...", 100F / steps);
		File nativesDir = new File(pack.getBinDir(), "natives");
		if (!nativesDir.exists()) {
			return true;
		}
		//Empty dir
		if (nativesDir.listFiles().length == 0) {
			return true;
		}
		stateChanged("Checking for Minecraft update...", 200F / steps);
		File minecraft = new File(pack.getBinDir(), "minecraft.jar");
		if (!minecraft.exists()) {
			return true;
		}
		stateChanged("Checking for Minecraft update...", 300F / steps);
		File lib = new File(pack.getBinDir(), "jinput.jar");
		if (!lib.exists()) {
			return true;
		}
		stateChanged("Checking for Minecraft update...", 400F / steps);
		lib = new File(pack.getBinDir(), "lwjgl.jar");
		if (!lib.exists()) {
			return true;
		}
		stateChanged("Checking for Minecraft update...", 500F / steps);
		lib = new File(pack.getBinDir(), "lwjgl_util.jar");
		if (!lib.exists()) {
			return true;
		}
		stateChanged("Checking for Minecraft update...", 600F / steps);
		String installed = Settings.getInstalledMC(build.getName());
		stateChanged("Checking for Minecraft update...", 700F / steps);
		String required = build.getMinecraftVersion();
		return installed == null || !installed.equals(required);
	}

	public void updateMinecraft(Modpack build) throws IOException {
		pack.init();

		String minecraft = build.getMinecraftVersion();
		String minecraftMD5 = build.getMinecraftMd5();

		// Processs minecraft.jar
		logger.info("Mod pack Build: " + build.getBuild() + " Minecraft Version: " + minecraft);
		File cache = new File(Utils.getCacheDirectory(), "minecraft_" + minecraft + ".jar");
		if (!cache.exists() || (minecraftMD5 == null || !minecraftMD5.equals(MD5Utils.getMD5(cache)))) {
			String output = pack.getCacheDir() + File.separator + "minecraft.jar";
			MinecraftDownloadUtils.downloadMinecraft(output, pack, build, listener);
		}
		Utils.copy(cache, new File(pack.getBinDir(), "minecraft.jar"));

		// Process forge library downloads ahead of time
		String fmlZip = RestAPI.getFmlLibZip(minecraft);
		if (RestAPI.getFmlLibZip(minecraft) != null) {
			File forgeCache = new File(Utils.getCacheDirectory(), fmlZip);
			if (!forgeCache.exists()) {
				String output = new File(pack.getCacheDir(), fmlZip).getAbsolutePath();
				DownloadUtils.downloadFile(RestAPI.getFmlLibURL() + "/" + fmlZip, output, fmlZip, null, listener);
			}
			FileUtils.unzipFile(forgeCache, new File(pack.getPackDirectory(), "lib"), listener);
		}

		// Process lwjgl downloads
		String lwjgl = "2.4.2"; // TODO: Get this from the API/Settings

		File lwjglCache = new File(Utils.getCacheDirectory(), "lwjgl-jar-" + lwjgl + ".zip");
		if (!lwjglCache.exists()) {
			String output = new File(pack.getCacheDir(), "lwjgl-jar-" + lwjgl + ".zip").getAbsolutePath();
			DownloadUtils.downloadFile(RestAPI.getLwjglURL(lwjgl), output, "lwjgl-jar-" + lwjgl + ".zip", null, listener);
		}
		FileUtils.unzipFile(lwjglCache, pack.getBinDir(), listener);

		String os = OperatingSystem.getNativeValue();
		File nativesCache = new File(Utils.getCacheDirectory(), "lwjgl-natives-" + os + "-" + lwjgl + ".zip");
		File natives = new File(pack.getBinDir(), "natives");
		natives.mkdir();

		if (!nativesCache.exists()) {
			String output = new File(pack.getCacheDir(), "lwjgl-natives-" + os + "-" + lwjgl + ".zip").getAbsolutePath();
			DownloadUtils.downloadFile(RestAPI.getLwjglNativeURL(lwjgl, os), output, "lwjgl-natives-" + os + "-" + lwjgl + ".zip", null, listener);
		}
		FileUtils.unzipFile(nativesCache, natives, listener);

		Settings.setInstalledMC(build.getName(), build.getMinecraftVersion());
		Settings.getYAML().save();
	}

	public void updateModpack(Modpack modpack) throws IOException {
		cleanupBinFolders(pack);
		cleanupModsFolders(pack);
		File workingDir = pack.getPackDirectory();

		pack.getCacheDir().mkdirs();
		pack.getConfigDir().mkdirs();

		File temp = pack.getCacheDir();

		List<Mod> mods = build.getMods();
		for (Mod mod : mods) {
			String name = mod.getName();
			String build = mod.getVersion();

			File modFile = new File(temp, name + "-" + build + ".zip");

			boolean shouldDownload = true;
			if (modFile.exists()) {
				System.out.println("Mod " + modFile.getName() + " already found in the cache.");
				String resultMD5 = MD5Utils.getMD5(modFile);
				System.out.println("Expected MD5: " + mod.getMD5() + " Calculated MD5: " + resultMD5);
				if (MD5Utils.getMD5(modFile).equalsIgnoreCase(mod.getMD5())) {
					shouldDownload = false;
					System.out.println("Succesfully skipped download of: " + mod.getName() + " " + mod.getVersion());
				}
			}

			if (shouldDownload) {
				DownloadUtils.downloadFile(mod.getURL(), modFile.getAbsolutePath(), null, mod.getMD5(), listener);
			}

			FileUtils.unzipFile(modFile, workingDir, listener);
		}
		cleanupPackCache(modpack);

		File installed = new File(pack.getBinDir(), "installed");
		if (!installed.exists()) {
			installed.createNewFile();
			Utils.pingURL(RestAPI.getDownloadCountURL(modpack.getName()));
		}
		YAMLProcessor yaml = new YAMLProcessor(installed, false, YAMLFormat.EXTENDED);
		yaml.setProperty("build", modpack.getBuild());
		yaml.save();
	}

	public void cleanupPackCache(Modpack modpack) {
		if (!pack.getCacheDir().isDirectory()) {
			return;
		}

		File[] files = pack.getCacheDir().listFiles();
		List<String> keepFiles = new ArrayList<String>(modpack.getMods().size());
		for (Mod mod : modpack.getMods()) {
			keepFiles.add(mod.getName() + "-" + mod.getVersion() + ".zip");
		}
		keepFiles.add("minecraft.jar");
		keepFiles.add("natives.jar");

		for (File file : files) {
			String fileName = file.getName();
			if (keepFiles.contains(fileName)) {
				continue;
			}
			FileUtils.deleteQuietly(file);
		}
	}

	public static void cleanupBinFolders(PackInfo pack) {
		try {
			if (!pack.getBinDir().exists()) {
				return;
			}

			HashSet<String> neededBinFiles = new HashSet<String>(Arrays.asList(new String[]{"modpack.jar", "minecraft.jar", "lwjgl.jar", "lwjgl_util.jar", "jinput.jar"}));
			for (File file : pack.getBinDir().listFiles()) {
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

	public static void cleanupModsFolders(PackInfo pack) {
		try {
			File working = pack.getPackDirectory();
			File mods = new File(working, "mods");
			if (mods.exists()) {
				FileUtils.cleanDirectory(mods, false);
			}

			File coremods = new File(working, "coremods");
			if (coremods.exists()) {
				FileUtils.cleanDirectory(coremods);
			}

			File resources = new File(working, "resources");
			if (resources.exists()) {
				FileUtils.cleanDirectory(resources);
			}
		} catch (IOException e) {
			System.err.println("Error while cleaning mods folders: ");
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
		@Override
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
