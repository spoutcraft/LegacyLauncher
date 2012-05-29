/*
 * This file is part of LauncherAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * LauncherAPI is licensed under the SpoutDev License Version 1.
 *
 * LauncherAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * LauncherAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
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

	public void addSkin(Skin skin);
}
