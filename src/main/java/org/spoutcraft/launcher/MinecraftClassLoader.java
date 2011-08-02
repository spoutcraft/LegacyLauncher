package org.spoutcraft.launcher;

import java.net.URL;
import java.net.URLClassLoader;

public class MinecraftClassLoader extends URLClassLoader{

	public MinecraftClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		// TODO Auto-generated method stub
		String modifiedName = name.replace("//", "/");
		return super.findClass(modifiedName);
	}
	
	
	

}
