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

package org.spoutcraft.launcher.entrypoint;

import com.beust.jcommander.JCommander;
import net.technicpack.launchercore.util.Directories;
import net.technicpack.launchercore.util.OperatingSystem;
import net.technicpack.launchercore.util.Utils;
import org.apache.commons.io.IOUtils;
import org.spoutcraft.launcher.Launcher;
import org.spoutcraft.launcher.StartupParameters;
import org.spoutcraft.launcher.log.Console;
import org.spoutcraft.launcher.log.DateOutputFormatter;
import org.spoutcraft.launcher.log.LoggerOutputStream;
import org.spoutcraft.launcher.log.RotatingFileHandler;
import org.spoutcraft.launcher.settings.LauncherDirectories;
import org.spoutcraft.launcher.skin.LauncherFrame;
import org.spoutcraft.launcher.skin.SplashScreen;
import org.spoutcraft.launcher.util.ShutdownThread;

import javax.swing.UIManager;
import java.awt.Toolkit;
import java.io.File;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SpoutcraftLauncher {
	public static StartupParameters params;
	protected static RotatingFileHandler handler = null;
	protected static Console console;
	private static Logger logger = null;

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		LauncherDirectories directories = new LauncherDirectories();
		Directories.instance = directories;

		// Prefer IPv4
		System.setProperty("java.net.preferIPv4Stack", "true");

		// Tell forge 1.5 to download from our mirror instead
		System.setProperty("fml.core.libraries.mirror", "http://mirror.technicpack.net/Technic/lib/fml/%s");

		params = setupParameters(args);

		cleanup();

		SplashScreen splash = new SplashScreen(Toolkit.getDefaultToolkit().getImage(SplashScreen.class.getResource("/org/spoutcraft/launcher/resources/splash.png")));
		splash.setVisible(true);
		directories.setSplashScreen(splash);
		setLookAndFeel();

		SpoutcraftLauncher.logger = setupLogger();

		int launcherBuild = parseInt(getLauncherBuild(), -1);
		logger.info("------------------------------------------");
		logger.info("Technic Launcher is starting....");
		logger.info("Launcher Build: " + launcherBuild);

		params.logParameters(logger);

		console = new Console(params.isConsole());

		Runtime.getRuntime().addShutdownHook(new ShutdownThread(console));

		// Set up the launcher and load login frame
		Launcher launcher = new Launcher();
		LauncherFrame frame = Launcher.getFrame();

		splash.dispose();
		frame.setVisible(true);

		logger.info("Launcher took: " + (System.currentTimeMillis() - start) + "ms to start");
	}

	public static String getLauncherBuild() {
		String build = "0";
		try {
			build = IOUtils.toString(SpoutcraftLauncher.class.getResource("/org/spoutcraft/launcher/resources/version").openStream(), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return build;
	}

	private static StartupParameters setupParameters(String[] args) {
		StartupParameters params = new StartupParameters(args);
		try {
			new JCommander(params, args);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		params.setupProxy();

		return params;
	}

	protected static Logger setupLogger() {
		final Logger logger = Logger.getLogger("net.technicpack.launcher.Main");
		File logDirectory = new File(Utils.getLauncherDirectory(), "logs");
		if (!logDirectory.exists()) {
			logDirectory.mkdir();
		}
		File logs = new File(logDirectory, "techniclauncher_%D.log");
		RotatingFileHandler fileHandler = new RotatingFileHandler(logs.getPath());

		fileHandler.setFormatter(new DateOutputFormatter(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")));

		for (Handler h : logger.getHandlers()) {
			logger.removeHandler(h);
		}
		logger.addHandler(fileHandler);

		SpoutcraftLauncher.handler = fileHandler;

		if (params != null && !params.isDebugMode()) {
			logger.setUseParentHandlers(false);

			System.setOut(new PrintStream(new LoggerOutputStream(console, Level.INFO, logger), true));
			System.setErr(new PrintStream(new LoggerOutputStream(console, Level.SEVERE, logger), true));
		}

		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				logger.log(Level.SEVERE, "Unhandled Exception in " + t, e);
			}
		});

		return logger;
	}

	private static int parseInt(String s, int def) {
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return def;
		}
	}

	private static void setLookAndFeel() {
		OperatingSystem os = OperatingSystem.getOperatingSystem();
		if (os.equals(OperatingSystem.OSX)) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Technic Launcher");
		}
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			logger.log(Level.WARNING, "Failed to setup look and feel", e);
		}
	}

	private static void cleanup() {
		File temp = new File(Utils.getLauncherDirectory(), "temp.jar");
		temp.delete();
		temp = new File(Utils.getLauncherDirectory(), "temp.exe");
		temp.delete();
	}

	public static void setupConsole() {
		console.setupConsole();
	}

	public static void destroyConsole() {
		console.destroyConsole();
	}
}

