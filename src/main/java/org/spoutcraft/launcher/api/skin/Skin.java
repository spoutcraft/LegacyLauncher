package org.spoutcraft.launcher.api.skin;

import java.io.File;

public interface Skin {
	
	public SkinDescriptionFile getDescription();
	
	public File getDataFolder();

	public boolean isEnabled();

	public CommonSkinManager getSkinLoader();
	
	public void onEnable();
	
	public void onDisable();
	
}
