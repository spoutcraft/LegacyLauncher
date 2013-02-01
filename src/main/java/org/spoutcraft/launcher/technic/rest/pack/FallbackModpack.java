package org.spoutcraft.launcher.technic.rest.pack;

import java.util.List;

import org.spoutcraft.launcher.Settings;
import org.spoutcraft.launcher.technic.rest.Mod;
import org.spoutcraft.launcher.technic.rest.Modpack;

public class FallbackModpack extends Modpack {
	private final String name;
	private final String build;

	public FallbackModpack(String name, String build) {
		this.name = name;
		this.build = build;
	}

	@Override
	public String getMinecraftVersion() {
		return Settings.getInstalledMC(name);
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
		// TODO Auto-generated method stub
		return null;
	}

}
