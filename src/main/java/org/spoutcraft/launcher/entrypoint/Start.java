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
package org.spoutcraft.launcher.entrypoint;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
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

	private static void launch(String[] args) throws Exception {
		// Test for custom build (not official build)
		if (SpoutcraftLauncher.getLauncherBuild().equals("0")) {
			SpoutcraftLauncher.main(args);
			return;
		}

		// Test for exe relaunch
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

		SpoutcraftLauncher.setupParameters(args);
		YAMLProcessor settings = SpoutcraftLauncher.setupSettings();
		if (settings == null) {
			throw new NullPointerException("The YAMLProcessor object was null for settings.");
		}
		Settings.setYAML(settings);

		migrateFolders();

		int version = Integer.parseInt(SpoutcraftLauncher.getLauncherBuild());
		int latest = getLatestLauncherBuild();
		if (version < latest) {
			File codeSource = new File(URLDecoder.decode(Start.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8"));
			File temp;
			if (codeSource.getName().endsWith(".exe")) {
				temp = new File(Utils.getSystemTemporaryDirectory(), "temp.exe");
			} else {
				temp = new File(Utils.getSystemTemporaryDirectory(), "temp.jar");
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

	public static int getLatestLauncherBuild() {
		try {
			return requestLatestLauncherBuild();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	private static int requestLatestLauncherBuild() throws RestfulAPIException {
		String url = RestAPI.getLauncherURL(Settings.getLauncherChannel());
		InputStream stream = null;
		try {
			stream = RestAPI.getCachingInputStream(new URL(url), true);
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
