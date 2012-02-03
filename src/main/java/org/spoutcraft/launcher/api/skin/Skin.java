package org.spoutcraft.launcher.api.skin;

import org.spoutcraft.launcher.api.skin.gui.LoginFrame;

import java.io.File;

public interface Skin {
	
	public SkinDescriptionFile getDescription();
	
	public File getDataFolder();

	public boolean isEnabled();

	public SkinLoader getSkinLoader();
	
	public void onEnable();
	
	public void onDisable();

	public LoginFrame getLoginFrame();
	
	public File getFile();
	
}
