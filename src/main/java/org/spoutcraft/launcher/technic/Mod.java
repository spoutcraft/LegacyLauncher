package org.spoutcraft.launcher.technic;

import org.spoutcraft.launcher.exceptions.RestfulAPIException;

public class Mod {
	private final String name;
	private final String version;

	public Mod(String name, String version) {
		this.name = name;
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public String getMD5() throws RestfulAPIException {
		return TechnicRestAPI.getModMD5(name, version);
	}
	@Override
	public String toString() {
		return "{ Mod [name: " + name + ", version: " + version + "] }";
	}
}
