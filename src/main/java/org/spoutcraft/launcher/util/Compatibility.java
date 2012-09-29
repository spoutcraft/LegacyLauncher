package org.spoutcraft.launcher.util;

import java.awt.Image;
import java.awt.Window;
import java.lang.reflect.Method;
import java.net.URI;
import org.spoutcraft.launcher.Settings;

/**
 * Static utility class meant to allow Java 1.6 calls while maining 1.5 compability
 */
public class Compatibility {
	
	/**
	 * Replaces Desktop.getDesktop().browse(uri)
	 * 
	 * @param uri
	 */
	public static void browse(URI uri) {
		try {
			Object o = Class.forName("java.awt.Desktop").getMethod("getDesktop", new Class[0]).invoke(null, new Object[0]);
			o.getClass().getMethod("browse", new Class[]{URI.class}).invoke(o, new Object[]{uri});
		} catch (Exception e) {
			if (Settings.isDebugMode()) {
				e.printStackTrace();
			}
		}	
	}
	
	@SuppressWarnings("rawtypes")
	public static void setIconImage(Window window, Image image) {
		try {
			Class[] params = {Image.class};
			Method setIconImage = Window.class.getMethod("setIconImage", params);
			setIconImage.invoke(window, image);
		} catch (Exception e) {
			if (Settings.isDebugMode()) {
				e.printStackTrace();
			}
		}	
	}
}
