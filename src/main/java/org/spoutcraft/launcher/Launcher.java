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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.logging.Level;

import net.technicpack.launchercore.exception.RestfulAPIException;
import net.technicpack.launchercore.install.AddPack;
import net.technicpack.launchercore.install.InstalledPack;
import net.technicpack.launchercore.install.InstalledPacks;
import net.technicpack.launchercore.install.PackRefreshListener;
import net.technicpack.launchercore.install.ResourceInstaller;
import net.technicpack.launchercore.install.User;
import net.technicpack.launchercore.install.Users;
import net.technicpack.launchercore.restful.PackInfo;
import net.technicpack.launchercore.restful.RestObject;
import net.technicpack.launchercore.restful.platform.PlatformPackInfo;
import net.technicpack.launchercore.restful.solder.FullModpacks;
import net.technicpack.launchercore.restful.solder.Solder;
import net.technicpack.launchercore.restful.solder.SolderConstants;
import net.technicpack.launchercore.restful.solder.SolderPackInfo;
import net.technicpack.launchercore.util.Download;
import net.technicpack.launchercore.util.DownloadUtils;
import net.technicpack.launchercore.util.Utils;

import org.spoutcraft.launcher.entrypoint.SpoutcraftLauncher;
import org.spoutcraft.launcher.skin.LauncherFrame;
import org.spoutcraft.launcher.skin.LoginFrame;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Launcher implements PackRefreshListener {
	private static Launcher instance;
	private final LauncherFrame launcherFrame;
	private final LoginFrame loginFrame;
	private Users users;
	private InstalledPacks installedPacks;
	private InstallThread installThread;
	private ResourceInstaller assetInstaller;

	private LinkedList<Thread> startupTasks = new LinkedList<Thread>();

	public Launcher() {
		if (Launcher.instance != null) {
			throw new IllegalArgumentException("You can't have a duplicate launcher");
		}

		instance = this;
		
		users = Users.load();
		
		trackLauncher();

		if (users == null) {
			users = new Users();
		}

		installedPacks = InstalledPacks.load();

		if (installedPacks == null) {
			installedPacks = new InstalledPacks();
		}

		this.launcherFrame = new LauncherFrame();
		this.loginFrame = new LoginFrame();
		this.assetInstaller = new ResourceInstaller();

		loadDefaultPacks();
		loadInstalledPacks();
		loadForcedPack();
		installedPacks.add(new AddPack());

		Thread assets = new Thread("Assets Thread") {
			@Override
			public void run() {
				instance.updateAssets();
			}
		};

		Thread news = new Thread("News Thread") {
			@Override
			public void run() {
				launcherFrame.getNews().loadArticles();
			}
		};

		news.start();
		assets.start();

		Thread waitForPacksAndReload = new Thread("Wait for Packs & Reload") {
			@Override
			public void run() {
				try {
					for (Thread task : startupTasks) {
						task.join();
					}
				} catch (InterruptedException ex)
				{
					ex.printStackTrace();
				}

				launcherFrame.getSelector().redraw(false);
			}
		};

		waitForPacksAndReload.start();

		attemptFaceDownloadsAndNotifyFrames();
	}

	private void updateAssets() {
		assetInstaller.updateResources();
	}

	private void loadInstalledPacks() {
		for (final InstalledPack pack : installedPacks.getPacks()) {
			if (pack.isPlatform()) {
				Thread thread = new Thread(pack.getName() + " Info Loading Thread") {
					@Override
					public void run() {
						try {
							String name = pack.getName();
							PlatformPackInfo platformPackInfo = PlatformPackInfo.getPlatformPackInfo(name);
							PackInfo info = platformPackInfo;
							if (platformPackInfo.hasSolder()) {
								SolderPackInfo solderPackInfo = SolderPackInfo.getSolderPackInfo(platformPackInfo.getSolder(), name);
								info = solderPackInfo;
							}

							info.getLogo();
							info.getIcon();
							info.getBackground();
							pack.setInfo(info);
							pack.setRefreshListener(instance);
						} catch (RestfulAPIException e) {
							Utils.getLogger().log(Level.WARNING, "Unable to load platform pack " + pack.getName(), e);
							pack.setLocalOnly();
							pack.setRefreshListener(instance);
						}
					}
				};
				startupTasks.add(thread);
				thread.start();
			}
		}
	}

	private void loadForcedPack() {
		if (SpoutcraftLauncher.params != null && SpoutcraftLauncher.params.getSolderPack() != null) {
			final String solder = SpoutcraftLauncher.params.getSolderPack();
			Thread thread = new Thread("Forced Solder Thread - " + solder) {

				@Override
				public void run() {
					try {
						SolderPackInfo info = SolderPackInfo.getSolderPackInfo(solder);
						if (info == null) {
							throw new RestfulAPIException();
						}

						info.getLogo();
						info.getIcon();
						info.getBackground();

						InstalledPacks packs = Launcher.getInstalledPacks();
						if (packs.getInstalledPacks().containsKey(info.getName())) {
							InstalledPack pack = installedPacks.get(info.getName());
							pack.setInfo(info);
						} else {
							InstalledPack pack = new InstalledPack(info.getName(), true);
							pack.setRefreshListener(instance);
							pack.setInfo(info);
							packs.add(pack);
						}
					} catch (RestfulAPIException e) {
						Utils.getLogger().log(Level.WARNING, "Unable to load forced solder pack " + solder, e);
					}
				}
			};
			startupTasks.add(thread);
			thread.start();
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
							pack.setRefreshListener(instance);
							pack.setInfo(info);
							packs.add(pack);
						}
						packs.reorder(index, name);
						index++;
					}
				} catch (RestfulAPIException e) {
					Utils.getLogger().log(Level.WARNING, "Unable to load technic modpacks", e);

					for (InstalledPack pack : packs.getPacks())
					{
						if (!pack.isPlatform() && pack.getInfo() == null && pack.getName() != null)
							pack.setLocalOnly();
					}
				}
			}
		};
		startupTasks.add(thread);
		thread.start();
	}

	public static InstalledPacks getInstalledPacks() {
		return instance.installedPacks;
	}

	public static Launcher getInstance() {
		return instance;
	}

	public static void launch(User user, InstalledPack pack, String build) {
		instance.installThread = new InstallThread(user, pack, build);
		instance.installThread.start();
	}

	public static boolean isLaunching() {
		return instance.installThread != null && !instance.installThread.isFinished();
	}

	public static LauncherFrame getFrame() {
		return instance.launcherFrame;
	}

	public static LoginFrame getLoginFrame() {
		return instance.loginFrame;
	}

	public static Users getUsers() {
		return instance.users;
	}

	@Override
	public void refreshPack(InstalledPack pack) {
		launcherFrame.getSelector().redraw(true);
	}
	
	public static void trackLauncher() {
		File installed = new File(Utils.getSettingsDirectory(), "installed");
		if (!installed.exists()) {
			try {
				installed.createNewFile();
				Utils.sendTracking("installLauncher", "install", SpoutcraftLauncher.getLauncherBuild());
			} catch (IOException e) {
				e.printStackTrace();
				Utils.getLogger().log(Level.INFO, "Failed to create install tracking file");
			}
			
		}
		
		Utils.sendTracking("runLauncher", "run", SpoutcraftLauncher.getLauncherBuild());
	}

	public void attemptFaceDownloadsAndNotifyFrames() {
		final HashSet<Thread> faceThreads = new HashSet<Thread>();

		for (String username : Launcher.getUsers().getUsers()) {
			final User user = Launcher.getUsers().getUser(username);
			Thread faceThread = new Thread("Face Download: "+user.getDisplayName()) {
				@Override
				public void run() {
					user.downloadFaceImage();
				}
			};
			faceThreads.add(faceThread);
			faceThread.start();
		}

		Thread waitAndNotifyThread = new Thread("Face completion wait and notify") {
			@Override
			public void run() {
				try {
					for (Thread thread : faceThreads) {
						thread.join();
					}
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}

				launcherFrame.faceDownloadsComplete();
				loginFrame.faceDownloadsComplete();
			}
		};

		waitAndNotifyThread.start();
	}
}
