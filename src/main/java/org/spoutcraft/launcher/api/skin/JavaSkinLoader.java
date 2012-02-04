/*
 * This file is part of LauncherAPI (http://www.spout.org/).
 *
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
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

import org.spoutcraft.launcher.api.Launcher;
import org.spoutcraft.launcher.api.security.CommonSecurityManager;
import org.spoutcraft.launcher.api.skin.exceptions.InvalidDescriptionFileException;
import org.spoutcraft.launcher.api.skin.exceptions.InvalidSkinException;

public class JavaSkinLoader implements SkinLoader {

	final CommonSecurityManager manager;
	private final double key;
	private final Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
	private final Map<String, CommonClassLoader> loaders = new HashMap<String, CommonClassLoader>();

	public JavaSkinLoader(final CommonSecurityManager manager, final double key) {
		this.manager = manager;
		this.key = key;
	}

	public void enableSkin(Skin paramSkin) {
		if (!JavaSkin.class.isAssignableFrom(paramSkin.getClass()))
			throw new IllegalArgumentException("Cannot enable skin with this SkinLoader as it is of the wrong type!");
		if (!paramSkin.isEnabled()) {
			JavaSkin cp = (JavaSkin) paramSkin;
			String name = cp.getDescription().getName();

			if (!loaders.containsKey(name)) {
				loaders.put(name, (CommonClassLoader) cp.getClassLoader());
			}

			try {
				cp.setEnabled(true);
				cp.onEnable();
			} catch (Exception e) {
				Launcher.getLogger().log(Level.SEVERE, new StringBuilder().append("An error occured when enabling '").append(paramSkin.getDescription().getFullName()).append("': ").append(e.getMessage()).toString(), e);
			}
		}
	}

	public void disableSkin(Skin paramSkin) {
		if (!JavaSkin.class.isAssignableFrom(paramSkin.getClass()))
			throw new IllegalArgumentException("Cannot disable skin with this SkinLoader as it is of the wrong type!");
		if (paramSkin.isEnabled()) {
			JavaSkin js = (JavaSkin) paramSkin;
			String name = js.getDescription().getName();

			if (!loaders.containsKey(name)) {
				loaders.put(name, (CommonClassLoader) js.getClassLoader());
			}

			try {
				js.setEnabled(false);
				js.onDisable();
			} catch (Exception e) {
				Launcher.getLogger().log(Level.SEVERE, new StringBuilder().append("An error occurred when disabling skin '").append(paramSkin.getDescription().getFullName()).append("' : ").append(e.getMessage()).toString(), e);
			}
		}

	}

	public Skin loadSkin(File paramFile) throws InvalidSkinException, InvalidDescriptionFileException {
		JavaSkin result = null;
		SkinDescriptionFile desc = null;

		if (!paramFile.exists())
			throw new InvalidSkinException(new StringBuilder().append(paramFile.getName()).append(" does not exist!").toString());

		JarFile jar = null;
		InputStream in = null;
		try {
			jar = new JarFile(paramFile);
			JarEntry entry = jar.getJarEntry("skin.yml");

			if (entry == null)
				throw new InvalidSkinException("Jar has no skin.yml!");

			in = jar.getInputStream(entry);
			desc = new SkinDescriptionFile(in);
		} catch (IOException e) {
			throw new InvalidSkinException(e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (jar != null) {
				try {
					jar.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		File dataFolder = new File(paramFile.getParentFile(), desc.getName());

		CommonClassLoader loader = null;
		try {
			URL[] urls = new URL[1];
			urls[0] = paramFile.toURI().toURL();

			loader = new CommonClassLoader(this, urls, getClass().getClassLoader());
			Class<?> main = Class.forName(desc.getMain(), true, loader);
			Class<? extends JavaSkin> Skin = main.asSubclass(JavaSkin.class);

			boolean locked = manager.lock(key);

			Constructor<? extends JavaSkin> constructor = Skin.getConstructor();

			result = constructor.newInstance();

			result.initialize(this, desc, dataFolder, paramFile, loader);

			if (!locked)
				manager.unlock(key);
		} catch (Exception e) {
			throw new InvalidSkinException(e);
		}

		loaders.put(desc.getName(), loader);

		return result;
	}

	public Class<?> getClassByName(final String name) {
		Class<?> cached = classes.get(name);

		if (cached != null) {
			return cached;
		} else {
			for (String current : loaders.keySet()) {
				CommonClassLoader loader = loaders.get(current);

				try {
					cached = loader.findClass(name, false);
				} catch (ClassNotFoundException cnfe) {
				}
				if (cached != null) {
					return cached;
				}
			}
		}
		return null;
	}

	public void setClass(final String name, final Class<?> clazz) {
		if (!classes.containsKey(name)) {
			classes.put(name, clazz);
		}
	}

}
