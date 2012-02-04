/*
 * This file is part of Launcher (http://www.spout.org/).
 *
 * Launcher is licensed under the SpoutDev License Version 1.
 *
 * Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Launcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spoutcraft.launcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Random;
import javax.swing.UIManager;

import org.apache.commons.io.IOUtils;
import com.beust.jcommander.JCommander;

import org.spoutcraft.launcher.api.Launcher;
import org.spoutcraft.launcher.api.security.CommonSecurityManager;
import org.spoutcraft.launcher.api.skin.JavaSkin;
import org.spoutcraft.launcher.api.skin.Skin;
import org.spoutcraft.launcher.api.skin.SkinDescriptionFile;
import org.spoutcraft.launcher.api.skin.SkinLoader;
import org.spoutcraft.launcher.api.skin.SkinManager;
import org.spoutcraft.launcher.api.util.Utils;
import org.spoutcraft.launcher.api.util.YAMLFormat;
import org.spoutcraft.launcher.api.util.YAMLProcessor;
import org.spoutcraft.launcher.skin.DefaultSkin;
import org.spoutcraft.launcher.skin.DefaultSkinLoader;

public class Main {
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		StartupParameters params = new StartupParameters();
		try {
			new JCommander(params, args);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		Utils.setStartupParameters(params);

		System.out.println("------------------------------------------");
		System.out.println("The Spoutcraft Launcher is starting....");
		System.out.println("Spoutcraft Launcher Build: " + getBuild("launcher-version"));
		System.out.println("Launcher API Build: " + getBuild("api-version"));

		// Set up the directories \\
		Utils.getWorkingDirectory().mkdirs();
		File skinDir = new File(Utils.getWorkingDirectory(), "skins");
		File sc = new File(Utils.getWorkingDirectory(), "spoutcraft");
		sc.mkdir();
		skinDir.mkdirs();

		setLookAndFeel();

		YAMLProcessor settings = setupSettings();
		if (settings == null) {
			throw new NullPointerException("The YAMLProcessor object was null for settings.");
		}

		// Set up the Launcher and load skins \\
		new Launcher(new SimpleGameUpdater(), new SimpleGameLauncher());
		SkinManager skinManager = Launcher.getSkinManager();
		skinManager.loadSkins(skinDir);

		// Register the default skin with the SkinManager \\
		JavaSkin defaultSkin = new DefaultSkin();

		SkinDescriptionFile desc = new SkinDescriptionFile("default", "1.0", "org.spoutcraft.launcher.skin.DefaultSkin");
		defaultSkin.initialize(new DefaultSkinLoader((CommonSecurityManager)System.getSecurityManager(), (new Random()).nextDouble()), desc, null, null, null);
		skinManager.addSkin(defaultSkin);

		// Load Selected Skin \\
		Skin skin = skinManager.getSkin(settings.getString("skin", "default"));
		if (skin == null) {
			skin = skinManager.getSkin("skin");
			if (skin == null) {
				throw new RuntimeException("The default skin object could not be found. Shutting down");
			}
		}
		skinManager.enableSkin(skin);
		if (skinManager.getEnabledSkin() == null) {
			System.exit(-9);
		}

		System.out.println("Using Skin '" + skin.getDescription().getFullName() + "'");

		skin.getLoginFrame().setVisible(true);

		System.out.println("The Launcher took: " + (System.currentTimeMillis() - start) + "ms to start");
	}

	public static void setLookAndFeel() {
		if (Utils.getOperatingSystem() == Utils.OS.MAC_OS) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Spoutcraft");
		}

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("There was an error setting the Look and Feel: " + e);
		}
	}

	public static String getBuild(String buildFile) {
		String build = "-1";
		try {
			build = IOUtils.toString(Main.class.getResource(buildFile).openStream(), "UTF-8");
		} catch (Exception e) {

		}
		return build;
	}

	private static YAMLProcessor setupSettings() {
		File file = new File(Utils.getWorkingDirectory(), "settings.yml");

		if (!file.exists()) {
			try {
				InputStream input = Main.class.getResource("settings.yml").openStream();
				if (input != null) {
					FileOutputStream output = null;
					try {
						file.getParentFile().mkdirs();
						output = new FileOutputStream(file);
						byte[] buf = new byte[8192];
						int length;

						while ((length = input.read(buf)) > 0) {
							output.write(buf, 0, length);
						}

					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						try {
							input.close();
						} catch (Exception ignored) {
						}
						try {
							if (output != null)
								output.close();
						} catch (Exception e) {
						}
					}
				}
			} catch (Exception e) {

			}
		}

		return new YAMLProcessor(file, false, YAMLFormat.EXTENDED);
	}
}
