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

package org.spoutcraft.launcher;

import net.technicpack.launchercore.exception.RestfulAPIException;
import net.technicpack.launchercore.install.AddPack;
import net.technicpack.launchercore.install.InstalledPack;
import net.technicpack.launchercore.install.InstalledPacks;
import net.technicpack.launchercore.install.Users;
import net.technicpack.launchercore.restful.RestObject;
import net.technicpack.launchercore.restful.solder.FullModpacks;
import net.technicpack.launchercore.restful.solder.Solder;
import net.technicpack.launchercore.restful.solder.SolderConstants;
import net.technicpack.launchercore.restful.solder.SolderPackInfo;
import net.technicpack.launchercore.util.Utils;
import org.spoutcraft.launcher.skin.LauncherFrame;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

public class Launcher {
	private static Launcher instance;
	private final LauncherFrame launcherFrame;
	private Users users;
	private InstalledPacks installedPacks;

	public Launcher() {
		if (Launcher.instance != null) {
			throw new IllegalArgumentException("You can't have a duplicate launcher");
		}

		Utils.getLogger().addHandler(new ConsoleHandler());
		instance = this;

		users = new Users();
		installedPacks = InstalledPacks.load();

		if (installedPacks == null) {
			installedPacks = new InstalledPacks();
		}

		this.launcherFrame = new LauncherFrame();

		loadDefaultPacks();
		loadInstalledPacks();
		installedPacks.add(new AddPack());

		System.out.println(installedPacks);
	}

	private void loadInstalledPacks() {
		for (InstalledPack pack : installedPacks.getPacks()) {
			if (pack.isPlatform()) {
//				PlatformPackInfo info = PlatformPackInfo.getPlatformPackInfo(pack.getName());

			}
		}
	}

	private void loadDefaultPacks() {

		Thread thread = new Thread("Technic Solder Defaults") {
			@Override
			public void run() {
				int index = 0;

				InstalledPacks packs = Launcher.getInstalledPacks();
				try {
					FullModpacks technic = RestObject.getRestObject(FullModpacks.class, SolderConstants.getFullSolderUrl(SolderConstants.TECHNIC));
					Solder solder = new Solder(SolderConstants.TECHNIC, technic.getMirrorUrl());
					for (SolderPackInfo info : technic.getModpacks().values()) {
						String name = info.getName();
						info.setSolder(solder);
						if (packs.getInstalledPacks().containsKey(name)) {
							InstalledPack pack = installedPacks.get(info.getName());
							pack.setInfo(info);
						} else {
							InstalledPack pack = new InstalledPack(name, false);
							pack.setInfo(info);
							packs.add(pack);
						}
						packs.reorder(index, name);
						index++;
					}
					launcherFrame.getSelector().redraw(false);
				} catch (RestfulAPIException e) {
					Utils.getLogger().log(Level.WARNING, "Unable to load technic modpacks", e);
				}
			}
		};
		thread.start();
	}

	public static InstalledPacks getInstalledPacks() {
		return instance.installedPacks;
	}

	public static LauncherFrame getFrame() {
		return instance.launcherFrame;
	}

	public static Users getUsers() {
		return instance.users;
	}
}
