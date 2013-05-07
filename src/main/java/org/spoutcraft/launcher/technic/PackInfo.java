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

package org.spoutcraft.launcher.technic;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;

import org.spoutcraft.launcher.Settings;
import org.spoutcraft.launcher.api.Launcher;
import org.spoutcraft.launcher.technic.rest.Modpack;
import org.spoutcraft.launcher.technic.rest.RestObject;
import org.spoutcraft.launcher.technic.skin.ModpackOptions;
import org.spoutcraft.launcher.util.Download;
import org.spoutcraft.launcher.util.DownloadUtils;
import org.spoutcraft.launcher.util.ImageUtils;
import org.spoutcraft.launcher.util.MD5Utils;
import org.spoutcraft.launcher.util.ResourceUtils;
import org.spoutcraft.launcher.util.Utils;

public abstract class PackInfo extends RestObject {
	private static BufferedImage BACKUP_LOGO;
	private static BufferedImage BACKUP_BACKGROUND;
	private static BufferedImage BACKUP_ICON;
	private AtomicReference<BufferedImage> logo = new AtomicReference<BufferedImage>();
	private AtomicReference<BufferedImage> background = new AtomicReference<BufferedImage>();
	private AtomicReference<BufferedImage> icon = new AtomicReference<BufferedImage>();
	private HashMap<AtomicReference<BufferedImage>, AtomicReference<Boolean>> downloading = new HashMap<AtomicReference<BufferedImage>, AtomicReference<Boolean>>(3);

	// Directories
	private File installedDirectory;
	private File binDir;
	private File cacheDir;
	private File configDir;
	private File savesDir;
	private File tempDir;
	private File resourceDir;
	private File coremodsDir;

	public PackInfo() {
		downloading.put(logo, new AtomicReference<Boolean>(false));
		downloading.put(background, new AtomicReference<Boolean>(false));
		downloading.put(icon, new AtomicReference<Boolean>(false));
	}

	public abstract String getName();

	public abstract String getDisplayName();

	public abstract String getRecommended();

	public abstract String getLatest();

	public abstract List<String> getBuilds();

	public abstract Modpack getModpack();

	public boolean isLoading() {
		return true;
	}

	public boolean isForceDir() {
		return false;
	}

	public String getLogoURL() {
		return "";
	}

	public String getBackgroundURL() {
		return "";
	}

	public String getIconURL() {
		return "";
	}

	public String getLogoMD5() {
		return "";
	}

	public String getBackgroundMD5() {
		return "";
	}

	public String getIconMD5() {
		return "";
	}

	public void init() {
		String location = Settings.getPackDirectory(getName());
		
		if (location != null) {
			installedDirectory = new File(location);
			initDirectories();
		}
		
		String build = Settings.getModpackBuild(getName());
		
		if (build == null) {
			Settings.setModpackBuild(getName(), ModpackOptions.RECOMMENDED);
			Settings.getYAML().save();
		}
	}

	public String getBuild() {
		String build = Settings.getModpackBuild(getName());
		String saveBuild = build;
		if (ModpackOptions.LATEST.equals(build)) {
			build = getLatest();
			saveBuild = ModpackOptions.LATEST;
		} else if (ModpackOptions.RECOMMENDED.equals(build) || build == null) {
			build = getRecommended();
			saveBuild = ModpackOptions.RECOMMENDED;
		}
		
		Settings.setModpackBuild(getName(), saveBuild);
		Settings.getYAML().save();
		return build;
	}

	public void initDirectories() {
		binDir = new File(installedDirectory, "bin");
		cacheDir = new File(installedDirectory, "cache");
		configDir = new File(installedDirectory, "config");
		savesDir = new File(installedDirectory, "saves");
		tempDir = new File(installedDirectory, "temp");
		resourceDir = new File(installedDirectory, "resources");
		coremodsDir = new File(installedDirectory, "coremods");
		
		binDir.mkdirs();
		cacheDir.mkdirs();
		configDir.mkdirs();
		savesDir.mkdirs();
		tempDir.mkdirs();
		resourceDir.mkdirs();
		coremodsDir.mkdirs();
	}
	
	public void setPackDirectory(File packPath) {
		if (installedDirectory != null) {
			try {
				org.apache.commons.io.FileUtils.copyDirectory(installedDirectory, packPath);
				org.apache.commons.io.FileUtils.cleanDirectory(installedDirectory);
			} catch (IOException e) {
				Launcher.getLogger().log(Level.SEVERE, "Unable to move modpack directory at " + installedDirectory.getAbsolutePath() + " to " + packPath.getAbsolutePath(), e);
				return;
			}
		}
		Settings.setPackDirectory(getName(), packPath);
		installedDirectory = packPath;
		initDirectories();
	}
	
	public File getPackDirectory() {
		if (installedDirectory == null) {
			setPackDirectory(new File(Utils.getLauncherDirectory(), getName()));
		}
		return installedDirectory;
	}
	
	public File getBinDir() {
		return binDir;
	}
	
	public File getCacheDir() {
		return cacheDir;
	}
	
	public File getConfigDir() {
		return configDir;
	}
	
	public File getSavesDir() {
		return savesDir;
	}
	
	public File getTempDir() {
		return tempDir;
	}
	
	public File getresourceDir() {
		return resourceDir;
	}

	public synchronized BufferedImage getLogo() {
		if (logo.get() != null) {
			return logo.get();
		} else {
			if (buildImage(logo, "logo.png", getLogoURL(), getLogoMD5())) {
				return logo.get();
			}
		}

		if (BACKUP_LOGO == null) {
			BACKUP_LOGO = loadBackup("/org/spoutcraft/launcher/resources/noLogo.png");
		}
		return BACKUP_LOGO;
	}

	public synchronized BufferedImage getBackground() {
		if (background.get() != null) {
			return background.get();
		} else {
			if (buildImage(background, "background.jpg", getBackgroundURL(), getBackgroundMD5(), 880, 520)) {
				Launcher.getFrame().getBackgroundImage().checkEnableTekkit(getName());
				return background.get();
			}
		}

		if (BACKUP_BACKGROUND == null) {
			BACKUP_BACKGROUND = ImageUtils.scaleImage(loadBackup("/org/spoutcraft/launcher/resources/background.jpg"), 880, 520);
		}
		return BACKUP_BACKGROUND;
	}

	public synchronized BufferedImage getIcon() {
		if (icon.get() != null) {
			return icon.get();
		} else {
			if (buildImage(icon, "icon.png", getIconURL(), getIconMD5())) {
				return icon.get();
			}
		}

		if (BACKUP_ICON == null) {
			BACKUP_ICON = loadBackup("/org/spoutcraft/launcher/resources/icon.png");
		}
		return BACKUP_ICON;
	}

	private BufferedImage loadBackup(String backup) {
		try {
			return ImageIO.read(ResourceUtils.getResourceAsStream(backup));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private boolean buildImage(AtomicReference<BufferedImage> image, String name, String url, String md5) {
		return buildImage(image, name, url, md5, 0, 0);
	}

	private boolean buildImage(AtomicReference<BufferedImage> image, String name, String url, String md5, int width, int height) {
		File assets = new File(Utils.getAssetsDirectory(), "packs");
		File packs = new File(assets, getName());
		packs.mkdirs();
		File temp = new File(packs, name);

		try {
			if (temp.exists() && (url.isEmpty() || md5.equals("") || MD5Utils.getMD5(temp).equalsIgnoreCase(md5))) {
				BufferedImage newImage;
				if (width > 0 && height > 0) {
					newImage = ImageUtils.scaleImage(ImageIO.read(temp), width, height);
				} else {
					newImage = ImageIO.read(temp);
				}
				image.set(newImage);
				return true; // We have successfully loaded the one from the file, with the correct md5
			}
		} catch (IIOException e) {
			Launcher.getLogger().log(Level.INFO, "Failed to load image " + temp.getAbsolutePath() + " from file, attempting download");
		} catch (IOException e) {
			e.printStackTrace(); // Failed to load image from file for some reason, continue on and debug the stack trace
		}

		downloadImage(image, url, temp, width, height, md5);
		return false;
	}

	private void downloadImage(final AtomicReference<BufferedImage> image, final String url, final File temp, final int width, final int height, final String md5) {
		if (url.isEmpty() || downloading.get(image).get()) {
			return;
		}

		downloading.get(image).set(true);
		final String name = getName();
		Thread thread = new Thread(name + " Image Download Worker") {
			@Override
			public void run() {
				try {
					if (temp.exists()) {
						System.out.println("Pack: " + getName() + " Calculated MD5: " + MD5Utils.getMD5(temp) + " Required MD5: " + md5);
					}
					Download download = DownloadUtils.downloadFile(url, temp.getAbsolutePath());
					BufferedImage newImage;
					boolean force = false;
					if (width > 0 && height > 0) {
						newImage = ImageUtils.scaleImage(ImageIO.read(download.getOutFile()), width, height);
						if (Launcher.getFrame().getSelector().getSelectedPack().getName().equals(name)) {
							force = true; // Force background fade in if the pack is selected and this is a background 
							// (It is a background because width/height are being set. Bad I know.)
						}
					} else {
						newImage = ImageIO.read(download.getOutFile());
					}
					image.set(newImage);
					Launcher.getFrame().getBackgroundImage().checkEnableTekkit(name);
					Launcher.getFrame().getSelector().redraw(force);
					downloading.get(image).set(false);
				} catch (IOException e) {
					System.out.println("Failed to download and load image from: " + url);
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PackInfo) {
			return ((File) obj).getName().equals(this.getName());
		}
		return false;
	}
}
