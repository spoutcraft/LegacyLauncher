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

import java.applet.Applet;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.spoutcraft.launcher.exception.CorruptedMinecraftJarException;

public class Launcher {
	
	@SuppressWarnings("rawtypes")
	public static Applet getMinecraftApplet() throws CorruptedMinecraftJarException{
			   
		File mcBinFolder = new File(PlatformUtils.getWorkingDirectory(), "bin");
		
		File spoutcraftJar = new File(mcBinFolder, "spoutcraft.jar");
		File minecraftJar = new File(mcBinFolder, "minecraft.jar");
		File jinputJar = new File(mcBinFolder, "jinput.jar");
		File lwglJar = new File(mcBinFolder, "lwjgl.jar");
		File lwjgl_utilJar = new File(mcBinFolder, "lwjgl_util.jar");
		
		
		
		SpoutcraftBuild build = SpoutcraftBuild.getSpoutcraftBuild();
		Map<String, Object> libraries = build.getLibraries();
		
		File[] files = new File[4 + libraries.size()];
		
		int index = 0;
		Iterator<Entry<String, Object>> i = libraries.entrySet().iterator();
		while (i.hasNext()) {
			Entry<String, Object> lib = i.next();
			String version = String.valueOf(lib.getValue());
			String name = lib.getKey() + "-" + version;
			File libraryFile = new File(mcBinFolder, name + ".jar");
			files[index] = libraryFile;
			index++;
		}

		URL urls[] = new URL[5];
		
		try {
			urls[0] = minecraftJar.toURI().toURL();
			files[index+0] = minecraftJar;
			urls[1] = jinputJar.toURI().toURL();
			files[index+1] = jinputJar;
			urls[2] = lwglJar.toURI().toURL();
			files[index+2] = lwglJar;
			urls[3] = lwjgl_utilJar.toURI().toURL();
			files[index+3] = lwjgl_utilJar;
			urls[4] = spoutcraftJar.toURI().toURL();

			ClassLoader classLoader = new MinecraftClassLoader(urls, ClassLoader.getSystemClassLoader(), spoutcraftJar, files);
			
			String nativesPath = new File(mcBinFolder, "natives").getAbsolutePath();
			System.setProperty("org.lwjgl.librarypath", nativesPath);
			System.setProperty("net.java.games.input.librarypath", nativesPath);
						
			Class minecraftClass = classLoader.loadClass("net.minecraft.client.MinecraftApplet");
			return (Applet) minecraftClass.newInstance();
			
		} catch (MalformedURLException ex) {
			ex.printStackTrace();
			return null;
		} catch (ClassNotFoundException ex) {
			throw new CorruptedMinecraftJarException(ex);
		} catch (IllegalAccessException ex) {
			throw new CorruptedMinecraftJarException(ex);
		} catch (InstantiationException ex) {
			throw new CorruptedMinecraftJarException(ex);
		}
	}
	
}
