package org.spoutcraft.launcher.skin;

import org.spoutcraft.launcher.api.security.CommonSecurityManager;
import org.spoutcraft.launcher.api.skin.Skin;
import org.spoutcraft.launcher.api.skin.JavaSkinLoader;
import org.spoutcraft.launcher.api.skin.exceptions.InvalidDescriptionFileException;
import org.spoutcraft.launcher.api.skin.exceptions.InvalidSkinException;

import java.io.File;

public class DefaultSkinLoader extends JavaSkinLoader {

	public DefaultSkinLoader(final CommonSecurityManager manager, final double key) {
		super(manager, key);
	}

	public void enableSkin(Skin paramSkin) {
	}

	public void disableSkin(Skin paramSkin) {
	}

	public Skin loadSkin(File paramFile) throws InvalidSkinException, InvalidDescriptionFileException {
		return null;
	}
}
