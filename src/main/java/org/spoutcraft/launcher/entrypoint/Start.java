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
package org.spoutcraft.launcher.entrypoint;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.UIManager;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;

import org.spoutcraft.launcher.Settings;
import org.spoutcraft.launcher.exceptions.RestfulAPIException;
import org.spoutcraft.launcher.rest.Project;
import org.spoutcraft.launcher.rest.RestAPI;
import org.spoutcraft.launcher.util.Download;
import org.spoutcraft.launcher.util.DownloadListener;
import org.spoutcraft.launcher.util.OperatingSystem;
import org.spoutcraft.launcher.util.Utils;
import org.spoutcraft.launcher.yml.YAMLProcessor;

public class Start {
	private static final ObjectMapper mapper = new ObjectMapper();
	public static void main(String[] args) {
		try {
			launch(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void launch(String[] args) throws Exception{
		// Text for local build (not official build)
		if (SpoutcraftLauncher.getLauncherBuild().equals("0")) {
			SpoutcraftLauncher.main(args);
			return;
		}
		// Test for exe relaunch
		SpoutcraftLauncher.setupLogger().info("Args: " + Arrays.toString(args));
		if (args.length > 0 && (args[0].equals("-Mover") || args[0].equals("-Launcher"))) {
			String[] argsCopy = new String[args.length - 1];
			for (int i = 1; i < args.length; i++) {
				argsCopy[i-1] = args[i];
			}
			if (args[0].equals("-Mover")) {
				Mover.main(argsCopy, true);
			} else {
				SpoutcraftLauncher.main(argsCopy);
			}
			return;
		}

		migrateFolders();

		YAMLProcessor settings = SpoutcraftLauncher.setupSettings();
		if (settings == null) {
			throw new NullPointerException("The YAMLProcessor object was null for settings.");
		}
		Settings.setYAML(settings);

		int version = Integer.parseInt(SpoutcraftLauncher.getLauncherBuild());
		int latest = getLatestLauncherBuild();
		if (version < latest) {
			File codeSource = new File(Start.class.getProtectionDomain().getCodeSource().getLocation().getPath());
			File temp;
			if (codeSource.getName().endsWith(".exe")) {
				temp = new File(Utils.getWorkingDirectory(), "temp.exe");
			} else {
				temp = new File(Utils.getWorkingDirectory(), "temp.jar");
			}

			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
			}

			ProgressSplashScreen splash = new ProgressSplashScreen();
			Download download = new Download(RestAPI.getLauncherDownloadURL(Settings.getLauncherChannel(), !codeSource.getName().endsWith(".exe")), temp.getPath());
			download.setListener(new LauncherDownloadListener(splash));
			download.run();

			ProcessBuilder processBuilder = new ProcessBuilder();
			ArrayList<String> commands = new ArrayList<String>();
			if (!codeSource.getName().endsWith(".exe")) {
				if (OperatingSystem.getOS().isWindows()) {
					commands.add("javaw");
				} else {
					commands.add("java");
				}
				commands.add("-Xmx256m");
				commands.add("-cp");
				commands.add(temp.getAbsolutePath());
				commands.add(Mover.class.getName());
			} else {
				commands.add(temp.getAbsolutePath());
				commands.add("-Mover");
			}
			commands.add(codeSource.getAbsolutePath());
			commands.addAll(Arrays.asList(args));
			processBuilder.command(commands);

			try {
				processBuilder.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.exit(0);
		} else {
			SpoutcraftLauncher.main(args);
		}
	}

	public static int getLatestLauncherBuild() throws RestfulAPIException {
		String url = RestAPI.getLauncherURL(Settings.getLauncherChannel());
		InputStream stream = null;
		try {
			URLConnection conn = (new URL(url)).openConnection();
			stream = conn.getInputStream();
			Project project = mapper.readValue(stream, Project.class);
			return project.getBuild();
		} catch (IOException e) {
			throw new RestfulAPIException("Error accessing URL [" + url + "]", e);
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}

	private static void migrateFolders() {
		File brokenSpoutcraftDir = Utils.getWorkingDirectory("Spoutcraft");
		if (brokenSpoutcraftDir.exists()) {
			File correctSpoutcraftDir = Utils.getWorkingDirectory();
			OperatingSystem os = OperatingSystem.getOS();
			if (os.isUnix() || os.isMac()) {
				try {
					FileUtils.copyDirectory(brokenSpoutcraftDir, correctSpoutcraftDir);
					FileUtils.deleteDirectory(brokenSpoutcraftDir);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static class LauncherDownloadListener implements DownloadListener {
		private final ProgressSplashScreen screen;
		LauncherDownloadListener(ProgressSplashScreen screen) {
			this.screen = screen;
		}

		public void stateChanged(String text, float progress) {
			screen.updateProgress((int)progress);
		}
	}
}
