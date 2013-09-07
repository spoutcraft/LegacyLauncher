/*
 * This file is part of Technic Launcher.
 * Copyright (C) 2013 Syndicate, LLC
 *
 * Technic Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Technic Launcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Technic Launcher.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.spoutcraft.launcher.util;

import org.apache.commons.io.IOUtils;
import org.spoutcraft.launcher.Settings;
import org.spoutcraft.launcher.StartupParameters;
import org.spoutcraft.launcher.entrypoint.SpoutcraftLauncher;
import org.spoutcraft.launcher.skin.SplashScreen;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class Utils {
	private static File workDir = null;
	private static StartupParameters params = null;
	private static SplashScreen splash = null;
	private static File settingsDir = null;

	public static File getLauncherDirectory() {
		if (workDir == null) {
			workDir = getWorkingDirectory("technic");
			boolean exists = workDir.exists();
			if (!exists && !workDir.mkdirs()) {
				throw new RuntimeException("The working directory could not be created: " + workDir);
			}

			settingsDir = workDir;
			SpoutcraftLauncher.setupSettings(settingsDir);

			if (Utils.getStartupParameters() != null && Utils.getStartupParameters().isPortable()) {
				return workDir;
			}

			if (Settings.getLauncherDir() != null) {
				File temp = new File(Settings.getLauncherDir());
				exists = temp.exists();
				if (exists) {
					workDir = temp;
				}
			}

			if (!exists) {
				workDir = selectInstallDir(workDir);
			} else if (Settings.getMigrate() && Settings.getMigrateDir() != null) {
				File migrate = new File(Settings.getMigrateDir());
				try {
					org.apache.commons.io.FileUtils.copyDirectory(workDir, migrate);
					org.apache.commons.io.FileUtils.cleanDirectory(workDir);
					workDir = migrate;

					File settings = new File(migrate, "settings.yml");
					settings.delete();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					Settings.removeMigrate();
				}
			}

			Settings.setLauncherDir(workDir.getAbsolutePath());
			Settings.getYAML().save();
		}
		return workDir;
	}

	public static File selectInstallDir(File workDir) {
		int result = JOptionPane.showConfirmDialog(splash, "No installation of technic found. \n\nTechnic Launcher will install at: \n" + workDir.getAbsolutePath() + " \n\nWould you like to change the install directory?", "Install Technic Launcher", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
		if (result == JOptionPane.YES_OPTION) {
			JFileChooser fileChooser = new JFileChooser(workDir);
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int changeInst = fileChooser.showOpenDialog(splash);

			if (changeInst == JFileChooser.APPROVE_OPTION) {
				workDir = fileChooser.getSelectedFile();
				if (!FileUtils.checkLaunchDirectory(workDir)) {
					JOptionPane.showMessageDialog(splash, "Please select an empty directory, or your default install folder with settings.yml in it.", "Invalid Location", JOptionPane.WARNING_MESSAGE);
					return selectInstallDir(workDir);
				}
			}
			workDir.mkdirs();
		}
		return workDir;
	}

	public static File getSettingsDirectory() {
		return settingsDir;
	}

	public static File getCacheDirectory() {
		File cache = new File(getLauncherDirectory(), "cache");
		if (!cache.exists()) {
			cache.mkdir();
		}
		return cache;
	}

	public static File getAssetsDirectory() {
		return new File(getLauncherDirectory(), "assets");
	}

	public static void setSplashScreen(SplashScreen splash) {
		Utils.splash = splash;
	}

	public static StartupParameters getStartupParameters() {
		return params;
	}

	public static void setStartupParameters(StartupParameters params) {
		Utils.params = params;
	}

	private static File getWorkingDirectory(String applicationName) {
		if (getStartupParameters() != null && getStartupParameters().isPortable()) {
			return new File(applicationName);
		}

		String userHome = System.getProperty("user.home", ".");
		File workingDirectory;

		OperatingSystem os = OperatingSystem.getOS();
		if (os.isUnix()) {
			workingDirectory = new File(userHome, '.' + applicationName + '/');
		} else if (os.isWindows()) {
			String applicationData = System.getenv("APPDATA");
			if (applicationData != null) {
				workingDirectory = new File(applicationData, "." + applicationName + '/');
			} else {
				workingDirectory = new File(userHome, '.' + applicationName + '/');
			}
		} else if (os.isMac()) {
			workingDirectory = new File(userHome, "Library/Application Support/" + applicationName);
		} else {
			workingDirectory = new File(userHome, applicationName + '/');
		}

		return workingDirectory;
	}

	public static boolean pingURL(String urlLoc) {
		InputStream stream = null;
		try {
			final URL url = new URL(urlLoc);
			final URLConnection conn = url.openConnection();
			conn.setConnectTimeout(10000);
			stream = conn.getInputStream();
			return true;
		} catch (IOException e) {
			return false;
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}
}
