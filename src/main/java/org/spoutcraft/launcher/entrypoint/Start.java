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

package org.spoutcraft.launcher.entrypoint;

import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.UIManager;

import org.spoutcraft.launcher.Settings;
import org.spoutcraft.launcher.technic.rest.RestAPI;
import org.spoutcraft.launcher.util.Download;
import org.spoutcraft.launcher.util.DownloadListener;
import org.spoutcraft.launcher.util.Utils;
import org.spoutcraft.launcher.util.OperatingSystem;

public class Start {
	public static void main(String[] args) {
		try {
			launch(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void launch(String[] args) throws Exception {
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

		Utils.getLauncherDirectory();
		int version = Integer.parseInt(SpoutcraftLauncher.getLauncherBuild());
		String buildStream = Settings.getBuildStream();
		int latest = RestAPI.getLatestLauncherBuild(buildStream);
		boolean update = false;
		
		if (buildStream.equals("beta") && version < latest) {
			update = true;
		} else if (buildStream.equals("stable") && version != latest) {
			update = true;
		}
		
		if (update) {
			File codeSource = new File(URLDecoder.decode(Start.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8"));
			File temp;
			if (codeSource.getName().endsWith(".exe")) {
				temp = new File(Utils.getSettingsDirectory(), "temp.exe");
			} else {
				temp = new File(Utils.getSettingsDirectory(), "temp.jar");
			}

			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
			}

			ProgressSplashScreen splash = new ProgressSplashScreen();
			Download download = new Download(RestAPI.getLauncherDownloadURL(latest, !codeSource.getName().endsWith(".exe")), temp.getPath());
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

	private static class LauncherDownloadListener implements DownloadListener {
		private final ProgressSplashScreen screen;
		LauncherDownloadListener(ProgressSplashScreen screen) {
			this.screen = screen;
		}

		@Override
		public void stateChanged(String text, float progress) {
			screen.updateProgress((int)progress);
		}
	}
}
