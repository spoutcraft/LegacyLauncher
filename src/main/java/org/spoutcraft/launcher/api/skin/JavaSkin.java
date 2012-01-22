package org.spoutcraft.launcher.api.skin;

import java.io.File;

public abstract class JavaSkin implements Skin {
	private SkinDescriptionFile desc = null;
	private File dataFolder;
	
	
	public SkinDescriptionFile getDescription() {
		return desc;
	}

	public File getDataFolder() {		
		return dataFolder;
	}

	public void initialize(JavaSkinLoader javaSkinLoader, SkinDescriptionFile desc2, File dataFolder, File paramFile, CommonClassLoader loader) {
		// TODO Auto-generated method stub
		
	}

	protected CommonClassLoader getClassLoader() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setEnabled(boolean b) {
		// TODO Auto-generated method stub
		
	}

	public void onDisable() {
		// TODO Auto-generated method stub
		
	}

	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	public void onEnable() {
		// TODO Auto-generated method stub
		
	}

	public CommonSkinManager getSkinLoader() {
		// TODO Auto-generated method stub
		return null;
	}
}
