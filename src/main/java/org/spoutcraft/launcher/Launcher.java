package org.spoutcraft.launcher;

import java.applet.Applet;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;



public class Launcher {
	
	@SuppressWarnings("rawtypes")
	public static Applet getMinecraftApplet() {
		       
		File mcBinFolder = new File(PlatformUtils.getWorkingDirectory(), "bin");
		
        File minecraftJar = new File(mcBinFolder, "minecraft.jar");
        File jinputJar = new File(mcBinFolder, "jinput.jar");
        File lwglJar = new File(mcBinFolder, "lwjgl.jar");
        File lwjgl_utilJar = new File(mcBinFolder, "lwjgl_util.jar");

        URL urls[] = new URL[4];
        try {
            urls[0] = minecraftJar.toURI().toURL();
            urls[1] = jinputJar.toURI().toURL();
            urls[2] = lwglJar.toURI().toURL();
            urls[3] = lwjgl_utilJar.toURI().toURL();

            ClassLoader classLoader = new MinecraftClassLoader(urls, ClassLoader.getSystemClassLoader());
            
            String nativesPath = new File(mcBinFolder, "natives").getAbsolutePath();
            System.setProperty("org.lwjgl.librarypath", nativesPath);
            System.setProperty("net.java.games.input.librarypath", nativesPath);
            
            Class minecraftClass = classLoader.loadClass("net.minecraft.client.MinecraftApplet");
            return (Applet) minecraftClass.newInstance();
            
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            return null;
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            return null;
        } catch (InstantiationException ex) {
            ex.printStackTrace();
            return null;
        }
    }
	
}
