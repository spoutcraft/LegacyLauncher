/*
 * This file is part of Spoutcraft Launcher (http://wiki.getspout.org/).
 * 
 * Spoutcraft Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spoutcraft Launcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.spoutcraft.launcher;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MinecraftClassLoader extends URLClassLoader{
	File spoutcraftBinDir = new File(GameUpdater.binDir + File.separator + "spoutcraft");
	private HashMap<String, String> spoutcraftClasses = new HashMap<String, String>();

	public MinecraftClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}
	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		String modifiedName = name.replace("//", "/");
		
		if (spoutcraftClasses.containsKey(modifiedName)) {
			modifiedName = spoutcraftClasses.get(modifiedName);
		}
		
		return super.findClass(modifiedName);
	}
}
