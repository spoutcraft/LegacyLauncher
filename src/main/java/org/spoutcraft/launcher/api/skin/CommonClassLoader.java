package org.spoutcraft.launcher.api.skin;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.spoutcraft.launcher.api.skin.exceptions.RestrictedClassException;

public class CommonClassLoader extends URLClassLoader {
	private final JavaSkinLoader loader;
	private final Map<String, Class<?>> classes = new HashMap<String, Class<?>>();

	public CommonClassLoader(final JavaSkinLoader loader, final URL[] urls, final ClassLoader parent) {
		super(urls, parent);
		this.loader = loader;
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		if (name.startsWith("javax.swing.SwingWorker"))
			throw new RestrictedClassException("Directly accessing 'javax.swing.SwingWorker' is not allowed!");
		if (name.startsWith("org.jdesktop.swingworker.SwingWorker"))
			throw new RestrictedClassException("Directly accessing 'org.jdesktop.swingworker.SwingWorker' is not allowed!");
		return findClass(name, true);
	}

	protected Class<?> findClass(String name, boolean checkGlobal) throws ClassNotFoundException {
		Class<?> result = classes.get(name);

		if (result == null) {
			if (checkGlobal) {
				result = loader.getClassByName(name);
			}

			if (result == null) {
				result = super.findClass(name);

				if (result != null) {
					loader.setClass(name, result);
				}
			}

			classes.put(name, result);
		}

		return result;
	}

	public Set<String> getClasses() {
		return classes.keySet();
	}
}
