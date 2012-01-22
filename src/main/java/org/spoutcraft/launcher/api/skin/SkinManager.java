package org.spoutcraft.launcher.api.skin;

import java.io.File;

import org.spoutcraft.launcher.api.skin.exceptions.InvalidDescriptionFileException;
import org.spoutcraft.launcher.api.skin.exceptions.InvalidSkinException;

public interface SkinManager {
	
	public Skin[] getSkins();
	
	public Skin getSkin(String name);
	
	public void loadSkins(File directory);
	
	public Skin loadSkin(File file) throws InvalidSkinException, InvalidDescriptionFileException;
	
	public void enableSkin(Skin skin);
	
	public void disableSkin(Skin skin);
	
	public void clearSkins();
	
	public Skin getEnabledSkin();

}
