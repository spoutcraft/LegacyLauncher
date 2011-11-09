package org.spoutcraft.launcher;

import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.internal.Lists;

public class Options {

	@Parameter
	private List<String> parameters = Lists.newArrayList();

	@Parameter(names = { "-username", "-user", "-u" }, description = "Minecraft Username")
	private String user = null;

	@Parameter(names = { "-password", "-pass", "-p" }, description = "Minecraft Password")
	private String pass = null;

	@Parameter(names = { "-server", "-host", "-join", "-server", "-j", "-h", "-s" }, description = "Minecraft Server to join")
	private String server = null;

	@Parameter(names = { "-portable", "--portable", "-pmode", "-portable_mode", "-pm" }, description = "Portable Mode")
	private boolean portable = false;

	@Parameter(names = { "-safe", "-smode", "-safe_mode", "-sm" }, description = "Safe Mode - Prevents Addons from being loaded")
	private boolean safe_mode = false;

	public List<String> getParameters() {
		return parameters;
	}

	public String getUser() {
		return user;
	}

	public String getPass() {
		return pass;
	}

	public String getServer() {
		if (server.contains(":")) {
			return server.substring(0, server.indexOf(":"));
		}
		return server;
	}

	public String getPort() {
		if (server.contains(":")) {
			return server.substring(server.indexOf(":") + 1);
		}
		return null;
	}

	public boolean isPortable() {
		return portable;
	}

	public boolean isSafe_mode() {
		return safe_mode;
	}

}
