package org.spoutcraft.launcher;

import java.net.URL;
import java.net.URLClassLoader;

public class MinecraftClassLoader extends URLClassLoader{

	public MinecraftClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		String modifiedName = name.replace("//", "/");
		return super.findClass(modifiedName);
	}
}
