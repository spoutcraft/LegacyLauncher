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

import java.io.File;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.internal.Lists;

import org.spoutcraft.launcher.api.util.Utils;

public final class StartupParameters {
	@Parameter
	private List<String> parameters = Lists.newArrayList();

	@Parameter(names = {"-username", "-user", "-u"}, description = "Minecraft Username")
	private String user = null;

	@Parameter(names = {"-password", "-pass", "-p"}, description = "Minecraft Password")
	private String pass = null;

	@Parameter(names = {"-server", "-host", "-join", "-j", "-h", "-s"}, description = "Minecraft Server to join")
	private String server = null;

	@Parameter(names = {"-portable", "--portable", "-pmode", "-portable_mode", "-pm"}, description = "Portable Mode")
	private boolean portable = false;

	@Parameter(names = {"-safe", "-smode", "-safe_mode", "-sm"}, description = "Safe Mode")
	private boolean safe_mode = false;

	@Parameter(names = {"-debug", "--debug", "-verbose", "-v", "-d"}, description = "Debug mode")
	private boolean debug = false;

	@Parameter(names = {"-proxy_host"}, description = "HTTP Proxy Host")
	private String proxyHost = null;

	@Parameter(names = {"-proxy_port"}, description = "HTTP Proxy Port")
	private String proxyPort = null;

	@Parameter(names = {"-proxy_user"}, description = "HTTP Proxy Username")
	private String proxyUser = null;

	@Parameter(names = {"-proxy_password"}, description = "HTTP Proxy Password")
	private String proxyPassword = null;

	@Parameter(names = {"-nomd5", "-ignoremd5"}, description = "Disables the MD5 checking of required files")
	private boolean ignoreMD5 = false;

	@Parameter(names = {"-build"}, description = "Uses a specific Spoutcraft build")
	private int build = -1;

	@Parameter(names = {"-relaunched"}, description = "Used to indicate the process has been relaunched for the property memory arguments")
	private boolean relaunched = false;

	public List<String> getParameters() {
		return parameters;
	}

	public void logParameters(Logger log) {
		log.info("------------ Startup Parameters ------------");
		if (user != null) {
			log.info("Minecraft Username: " + user);
		}
		if (pass != null) {
			log.info("Minecraft Password exists");
		}
		if (server != null) {
			log.info("Minecraft Server: " + server);
		}
		if (portable) {
			log.info("Portable mode activated");
		}
		if (safe_mode) {
			log.info("Safe mode activated");
		}
		if (debug) {
			log.info("Debug mode activated");
		}
		if (proxyHost != null) {
			log.info("Proxy Host: " + proxyHost);
		}
		if (proxyPort != null) {
			log.info("Proxy Port: " + proxyPort);
		}
		if (proxyUser != null) {
			log.info("Proxy User exists");
		}
		if (proxyPassword != null) {
			log.info("Porxy Password exists");
		}
		if (ignoreMD5) {
			log.info("No MD5 Mode activated");
		}
		if (build != -1) {
			log.info("Spoutcraft build selected: " + build);
		}
		if (relaunched) {
			log.info("Relaunched with correct memory");
		}
		log.info("--------- End of Startup Parameters ---------");
	}

	public boolean relaunch(Logger log) {
		if (!relaunched) {
			String pathToJar;
			File jar = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getFile());
			try {
				pathToJar = jar.getCanonicalPath();
			} catch (IOException e1) {
				pathToJar = jar.getAbsolutePath();
			}
			pathToJar = URLDecode.decode(pathToJar, "UTF-8");
			final int memory = Memory.getMemoryFromId(Settings.getMemory()).getMemoryMB();
			log.info("Attempting relaunch with " + memory + " mb of RAM");
			log.info("Path to Launcher Jar: " + pathToJar);

			ProcessBuilder processBuilder = new ProcessBuilder();
			ArrayList<String> commands = new ArrayList<String>();
			if (Utils.getOperatingSystem() == Utils.OS.WINDOWS) {
				commands.add("javaw");
			} else {
				commands.add("java");
			}
			commands.add("-Xmx" + memory + "m");
			commands.add("-cp");
			commands.add(pathToJar);
			commands.add(Main.class.getName());
			commands.addAll(this.parameters);
			commands.add("-relaunched");
			processBuilder.command(commands);

			try {
				processBuilder.start();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public String getUser() {
		return user;
	}

	public String getPass() {
		return pass;
	}

	public String getServer() {
		if (server == null) {
			return null;
		}
		if (server.contains(":")) {
			return server.substring(0, server.indexOf(":"));
		}
		return server;
	}

	public String getPort() {
		if (server == null) {
			return null;
		}
		if (server.contains(":")) {
			return server.substring(server.indexOf(":") + 1);
		}
		return null;
	}

	public boolean isPortable() {
		return portable;
	}

	public boolean isSafeMode() {
		return safe_mode;
	}

	public boolean isDebugMode() {
		return debug;
	}

	public boolean isIgnoreMD5() {
		return ignoreMD5;
	}

	public int getSpoutcraftBuild() {
		return build;
	}

	public void setupProxy() {
		if (proxyHost != null) {
			System.setProperty("http.proxyHost", proxyHost);
			System.setProperty("https.proxyHost", proxyHost);
			if (proxyPort != null) {
				System.setProperty("http.proxyPort", proxyPort);
				System.setProperty("https.proxyPort", proxyPort);
			}
		}
		if (proxyUser != null && proxyPassword != null) {
			Authenticator.setDefault(new ProxyAuthenticator(proxyUser, proxyPassword));
		}
	}

	private class ProxyAuthenticator extends Authenticator {
		final String user, pass;
		ProxyAuthenticator(String user, String pass) {
			this.user = user;
			this.pass = pass;
		}
		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(user, pass.toCharArray());
		}
	}
}
