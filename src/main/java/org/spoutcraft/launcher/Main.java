/*
 * This file is part of Spoutcraft Launcher (http://www.spout.org/).
 *
 * Spoutcraft Launcher is licensed under the SpoutDev License Version 1.
 *
 * Spoutcraft Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spoutcraft Launcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spoutcraft.launcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import javax.swing.UIManager;

import com.beust.jcommander.JCommander;

import org.spoutcraft.launcher.gui.LoginForm;
import org.spoutcraft.launcher.logs.SystemConsoleListener;

public class Main {
	static String[] args_temp;
	public static int build = -1;
	static File recursion;

	public Main() throws Exception {
		main(new String[0]);
	}

	public static void reboot(String memory) {
		try {
			String pathToJar = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
			ArrayList<String> params = new ArrayList<String>();
			if (PlatformUtils.getPlatform() == PlatformUtils.OS.windows) {
				params.add("javaw"); // Windows-specific
			} else {
				params.add("java"); // Linux/Mac/whatever
			}
			params.add(memory);
			params.add("-classpath");
			params.add(pathToJar);
			params.add("org.spoutcraft.launcher.Main");
			for (String arg : args_temp) {
				params.add(arg);
			}
			if (PlatformUtils.getPlatform() == PlatformUtils.OS.macos) {
				params.add("-Xdock:name=\"Spoutcraft\"");

				try {
					File icon = new File(PlatformUtils.getWorkingDirectory(), "icon.icns");
					GameUpdater.copy(Main.class.getResourceAsStream("/org/spoutcraft/launcher/icon.icns"), new FileOutputStream(icon));
					params.add("-Xdock:icon=" + icon.getCanonicalPath());
				} catch (Exception ignore) {
				}
			}
			ProcessBuilder pb = new ProcessBuilder(params);
			Process process = pb.start();
			if(process == null) {
				throw new Exception("!");
			}
			System.exit(0);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		Options options = new Options();
		try {
			new JCommander(options, args);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		MinecraftUtils.setOptions(options);

		recursion = new File(PlatformUtils.getWorkingDirectory(), "rtemp");

		args_temp = args;
		boolean relaunch = false;
		try {
			if (!recursion.exists()) {
				relaunch = true;
			} else {
				recursion.delete();
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}

		if (relaunch) {
			if (SettingsUtil.getMemorySelection() < 6) {
				int mem = 1 << (9 + SettingsUtil.getMemorySelection());
				recursion.createNewFile();
				reboot("-Xmx" + mem + "m");
			}
		}
		if (PlatformUtils.getPlatform() == PlatformUtils.OS.macos) {
			try{
				System.setProperty("apple.laf.useScreenMenuBar", "true");
				System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Spoutcraft");
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception ignore) { }
		}
		PlatformUtils.getWorkingDirectory().mkdirs();

		new File(PlatformUtils.getWorkingDirectory(), "spoutcraft").mkdir();

		SystemConsoleListener listener = new SystemConsoleListener();

		listener.initialize();

		System.out.println("------------------------------------------");
		System.out.println("Spoutcraft Launcher is starting....");
		System.out.println("Spoutcraft Launcher Build: " + getBuild());

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Warning: Can't get system LnF: " + e);
		}

		LoginForm login = new LoginForm();

		login.setVisible(true);
	}

	private static int getBuild() {
		if (build == -1) {
			File buildInfo = new File(PlatformUtils.getWorkingDirectory(), "launcherVersion");
			if (buildInfo.exists()) {
				try {
					BufferedReader bf = new BufferedReader(new FileReader(buildInfo));
					String version = bf.readLine();
					build = Integer.parseInt(version);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return build;
	}
}
