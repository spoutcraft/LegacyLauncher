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

import net.technicpack.launchercore.install.AvailablePackList;
import net.technicpack.launchercore.install.Version;
import net.technicpack.launchercore.install.InstalledPack;
import org.spoutcraft.launcher.launcher.InstalledPacks;
import net.technicpack.launchercore.util.Utils;
import org.spoutcraft.launcher.settings.OldSettings;
import net.technicpack.launchercore.util.Settings;
import org.spoutcraft.launcher.util.yml.YAMLFormat;
import org.spoutcraft.launcher.util.yml.YAMLProcessor;

import java.io.File;
import java.util.logging.Level;

public class MigrateUtils {

	@SuppressWarnings("deprecation")
	public static void migrateSettings() {
		File settingsFile = new File(Utils.getSettingsDirectory(), "settings.yml");
		if (!settingsFile.exists()) {
			Settings.load();
			return;
		}

		System.out.println("Old settings found, migrating...");

		YAMLProcessor settings = new YAMLProcessor(settingsFile, false, YAMLFormat.EXTENDED);
		OldSettings.setYAML(settings);

		Settings.setMemory(OldSettings.getMemory());
		Settings.setBuildStream(OldSettings.getBuildStream());
		Settings.setDirectory(OldSettings.getLauncherDir());
		Settings.setShowConsole(OldSettings.getShowLauncherConsole());

		AvailablePackList packList = new AvailablePackList(new InstalledPacks());

		for (String modpack : OldSettings.getInstalledPacks()) {
			boolean custom = OldSettings.isPackCustom(modpack);
			String build = OldSettings.getModpackBuild(modpack);
			String directory = OldSettings.getPackDirectory(modpack);

            if (directory != null)
            {
                InstalledPack pack = new InstalledPack(modpack, custom, build, directory);
                pack.setRefreshListener(packList);
                pack.getInstalledDirectory();
                migrateInstalled(pack);
                packList.add(pack);
            }
		}

		File lastLogin = new File(Utils.getSettingsDirectory(), "lastlogin");
		lastLogin.delete();
		settingsFile.delete();
		System.out.println("Finished migration");
		System.out.println(Settings.instance);
	}

	private static void migrateInstalled(InstalledPack pack) {
		File oldInstalled = new File(pack.getBinDir(), "installed");
		if (oldInstalled.exists()) {
			Version version = loadInstalled(oldInstalled);
			if (version != null) {
				version.save(pack.getBinDir());
			}
			oldInstalled.delete();
		}
	}

	private static Version loadInstalled(File installed) {
		try {
			YAMLProcessor yaml = new YAMLProcessor(installed, false, YAMLFormat.EXTENDED);
			yaml.load();
			String build = (String) yaml.getProperty("build");
			if (build == null || build.isEmpty()) {
				return null;
			}
			return new Version(build, true);
		} catch (Exception e) {
			Utils.getLogger().log(Level.WARNING, "Error migrating installed build file! " + installed.getAbsolutePath());
			return null;
		}
	}
}
