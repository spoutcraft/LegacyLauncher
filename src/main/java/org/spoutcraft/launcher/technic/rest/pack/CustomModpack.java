package org.spoutcraft.launcher.technic.rest.pack;

import java.util.ArrayList;
import java.util.List;

import org.spoutcraft.launcher.technic.rest.Mod;
import org.spoutcraft.launcher.technic.rest.Modpack;
import org.spoutcraft.launcher.technic.rest.info.CustomInfo;

public class CustomModpack extends Modpack {
	private final CustomInfo info;
	private final String name;
	private final String build;
	private final String minecraftVersion;

	public CustomModpack(CustomInfo info) {
		this.info = info;
		this.name = info.getName();
		this.build = info.getVersion();
		this.minecraftVersion = info.getMinecraftVersion();
	}

	@Override
	public String getMinecraftVersion() {
		return minecraftVersion;
	}

	@Override
	public String getBuild() {
		return build;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<Mod> getMods() {
		List<Mod> mods = new ArrayList<Mod>(1);
		mods.add(new Mod(getName(), getBuild(), getInfo().getURL()));
		return null;
	}

	public CustomInfo getInfo() {
		return info;
	}
}
