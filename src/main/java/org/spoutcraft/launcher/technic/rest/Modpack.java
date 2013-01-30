package org.spoutcraft.launcher.technic.rest;

import java.util.List;

public abstract class Modpack {

	public abstract String getMinecraftVersion();

	public abstract String getBuild();

	public abstract String getName();

	public abstract List<Mod> getMods();
}
