/*
 * This file is part of Spoutcraft Launcher.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
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
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spoutcraft.launcher;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;
import javax.swing.UIManager;

import org.apache.commons.io.IOUtils;
import com.beust.jcommander.JCommander;

import org.spoutcraft.launcher.api.Build;
import org.spoutcraft.launcher.api.Launcher;
import org.spoutcraft.launcher.api.SpoutcraftDirectories;
import org.spoutcraft.launcher.api.skin.JavaSkin;
import org.spoutcraft.launcher.api.util.Utils;
import org.spoutcraft.launcher.api.util.YAMLFormat;
import org.spoutcraft.launcher.api.util.YAMLProcessor;
import org.spoutcraft.launcher.skin.DefaultSkin;

public class Main {
	private static Logger logger = null;
	public Main() {
		main(new String[0]);
	}

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		final long startupTime = start;
		
		//Required for ROME to work
		ClassLoader cl = Main.class.getClassLoader();
		Thread.currentThread().setContextClassLoader(cl);
		
		StartupParameters params = setupParameters(args);
		setupLogger();

		int launcherBuild = parseInt(getBuild("version"), -1);
		logger.info("------------------------------------------");
		logger.info("Spoutcraft Launcher is starting....");
		logger.info("Launcher Build: " + launcherBuild);
		
		params.logParameters(logger);
		
		if (params.relaunch()) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) { }
			System.exit(0);
			return;
		}
		
		//Setup Directories
		SpoutcraftDirectories dirs = new SpoutcraftDirectories();
		dirs.getSkinDir().mkdirs();
		dirs.getSpoutcraftDir().mkdirs();

		setLookAndFeel();

		if (params.isDebugMode()) {
			logger.info("Initial launcher organization and look and feel time took " + (System.currentTimeMillis() - start)	 + " ms");
			start = System.currentTimeMillis();
		}

		YAMLProcessor settings = setupSettings();
		if (settings == null) {
			throw new NullPointerException("The YAMLProcessor object was null for settings.");
		}
		Settings.setSettings(settings);
		Settings.setLauncherSelectedBuild(launcherBuild);
		if (params.getSpoutcraftBuild() > 0) {
			Settings.setSpoutcraftSelectedBuild(params.getSpoutcraftBuild());
			Settings.setSpoutcraftBuild(Build.CUSTOM);
		}

		if (params.isDebugMode()) {
			logger.info("Launcher settings took " + (System.currentTimeMillis() - start)	 + " ms");
			start = System.currentTimeMillis();
		}

		// Set up the Launcher and load skins
		Launcher launcher = new Launcher(new SimpleGameUpdater(), new SimpleGameLauncher());
		((SimpleGameUpdater)Launcher.getGameUpdater()).start();

		if (params.isDebugMode()) {
			logger.info("Launcher skin manager took " + (System.currentTimeMillis() - start)	 + " ms");
			start = System.currentTimeMillis();
		}

		JavaSkin defaultSkin = new DefaultSkin();
		launcher.setSkin(defaultSkin);
		defaultSkin.getLoginFrame().setVisible(true);

		if (params.isDebugMode()) {
			logger.info("Launcher default skin loading took " + (System.currentTimeMillis() - start)	 + " ms");
			start = System.currentTimeMillis();
		}

		logger.info("Launcher took: " + (System.currentTimeMillis() - startupTime) + "ms to start");
	}

	@SuppressWarnings("restriction")
	private static void setLookAndFeel() {
		if (Utils.getOperatingSystem() == Utils.OS.MAC_OS) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Spoutcraft");
		}
		try {
			boolean laf = false;
			if (Utils.getOperatingSystem() == Utils.OS.WINDOWS) {
				// This bypasses the expensive reflection calls
				try { 
					UIManager.setLookAndFeel(new com.sun.java.swing.plaf.windows.WindowsLookAndFeel());
					laf = true;
				} catch (Exception ignore) { }
			}

			if (!laf) {
				// Can't guess the laf for other os's as easily
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Failed to setup look and feel", e);
		}
	}

	private static int parseInt(String s, int def) {
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return def;
		}
	}

	private static void setupLogger() {
		Logger logger = Logger.getLogger("launcher");
		File logDirectory = new File(Utils.getWorkingDirectory(), "logs");
		if (!logDirectory.exists()) {
			logDirectory.mkdir();
		}
		File logs = new File(logDirectory, "spoutcraft_%D.log");
		RotatingFileHandler fileHandler = new RotatingFileHandler(logs.getPath());

		fileHandler.setFormatter(new DateOutputFormatter(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")));

		for (Handler h : logger.getHandlers()) {
			logger.removeHandler(h);
		}
		logger.addHandler(fileHandler);

		logger.setUseParentHandlers(false);

		System.setOut(new PrintStream(new LoggerOutputStream(Level.INFO, logger), true));
		System.setErr(new PrintStream(new LoggerOutputStream(Level.SEVERE, logger), true));
		Main.logger = logger;
	}
	
	private static StartupParameters setupParameters(String[] args) {
		StartupParameters params = new StartupParameters();
		try {
			new JCommander(params, args);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		Utils.setStartupParameters(params);

		params.setupProxy();
		
		return params;
	}

	public static String getBuild(String buildFile) {
		String build = "-1";
		try {
			build = IOUtils.toString(Main.class.getResource("/" + buildFile).openStream(), "UTF-8");
		} catch (Exception e) {

		}
		return build;
	}

	private static YAMLProcessor setupSettings() {
		File file = new File(Utils.getWorkingDirectory(), "config" + File.separator + "settings.yml");

		if (!file.exists()) {
			try {
				InputStream input = Main.class.getResource("resources/settings.yml").openStream();
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
			} catch (Exception e) { }
		}

		return new YAMLProcessor(file, false, YAMLFormat.EXTENDED);
	}
}

class LoggerOutputStream extends ByteArrayOutputStream {
	private final String separator = System.getProperty("line.separator");
	private final Level level;
	private final Logger log;

	public LoggerOutputStream(Level level, Logger log) {
		super();
		this.level = level;
		this.log = log;
	}

	@Override
	public synchronized void flush() throws IOException {
		super.flush();
		String record = this.toString();
		super.reset();

		if (record.length() > 0 && !record.equals(separator)) {
			log.logp(level, "LoggerOutputStream", "log" + level, record);
		}
	}
}

class RotatingFileHandler extends StreamHandler {
	private final SimpleDateFormat date;
	private final String logFile;
	private String filename;

	public RotatingFileHandler(String logFile) {
		this.logFile = logFile;
		date = new SimpleDateFormat("yyyy-MM-dd");
		filename = calculateFilename();
		try {
			setOutputStream(new FileOutputStream(filename, true));
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public synchronized void flush() {
		if (!filename.equals(calculateFilename())) {
			filename = calculateFilename();
			try {
				setOutputStream(new FileOutputStream(filename, true));
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
			}
		}
		super.flush();
	}

	private String calculateFilename() {
		return logFile.replace("%D", date.format(new Date()));
	}
}

class DateOutputFormatter extends Formatter {
	private final SimpleDateFormat date;

	public DateOutputFormatter(SimpleDateFormat date) {
		this.date = date;
	}

	@Override
	public String format(LogRecord record) {
		StringBuilder builder = new StringBuilder();

		builder.append(date.format(record.getMillis()));
		builder.append(" [");
		builder.append(record.getLevel().getLocalizedName().toUpperCase());
		builder.append("] ");
		builder.append(formatMessage(record));
		builder.append('\n');

		if (record.getThrown() != null) {
			StringWriter writer = new StringWriter();
			record.getThrown().printStackTrace(new PrintWriter(writer));
			builder.append(writer.toString());
		}

		return builder.toString();
	}
}
