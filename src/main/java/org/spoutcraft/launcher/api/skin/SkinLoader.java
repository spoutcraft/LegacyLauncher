package org.spoutcraft.launcher.api.skin;

import java.io.File;

import org.spoutcraft.launcher.api.skin.exceptions.InvalidDescriptionFileException;
import org.spoutcraft.launcher.api.skin.exceptions.InvalidSkinException;

public interface SkinLoader {
	
	/**
	 * Enables the skin
	 *
	 * @param paramSkin
	 */
	public abstract void enableSkin(Skin paramSkin);

	/**
	 * Disables the skin
	 *
	 * @param paramSkin
	 */
	public abstract void disableSkin(Skin paramSkin);

	/**
	 * Loads the file as a skin
	 *
	 * @param paramFile
	 * @return instance of the skin
	 * @throws InvalidSkinException
	 * @throws InvalidSkinException
	 * @throws UnknownDependencyException
	 * @throws InvalidDescriptionFileException
	 */
	public abstract Skin loadSkin(File paramFile) throws InvalidSkinException, InvalidDescriptionFileException;
	
}
