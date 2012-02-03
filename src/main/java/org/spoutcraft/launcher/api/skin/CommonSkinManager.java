package org.spoutcraft.launcher.api.skin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.spoutcraft.launcher.api.Launcher;
import org.spoutcraft.launcher.api.security.CommonSecurityManager;
import org.spoutcraft.launcher.api.skin.exceptions.InvalidDescriptionFileException;
import org.spoutcraft.launcher.api.skin.exceptions.InvalidSkinException;
import org.spoutcraft.launcher.api.util.Utils;

public class CommonSkinManager implements SkinManager {

	private final CommonSecurityManager manager;
	private final double key;
	public final Map<String, Skin> names = new HashMap<String, Skin>();
	public final List<Skin> skins = new ArrayList<Skin>();
	public final SkinLoader javaLoader;
	public final SkinLoader yamlLoader;
	public Skin enabledSkin = null;
	
	
	public CommonSkinManager(final CommonSecurityManager manager, final double key) {
		this.manager = manager;
		this.key = key;
		this.javaLoader = new JavaSkinLoader(manager, key);
		this.yamlLoader = null;
	}

	public Skin[] getSkins() {
		return skins.toArray(new Skin[skins.size()]);
	}

	public Skin getSkin(String name) {
		return names.get(name);
	}

	public void loadSkins(File directory) {
		if (!directory.isDirectory())
			throw new IllegalArgumentException("File parameter was not a Directory!");

		for (File file : directory.listFiles()) {
			if (file.isFile()) {
				try {
					loadSkin(file);
				} catch (InvalidDescriptionFileException e) {
					safelyLog(Level.SEVERE, new StringBuilder().append("Unable to load '").append(file.getName()).append("' in directory '").append(file.getPath()).append("': ").append(e.getMessage()).toString(), e);
				} catch (InvalidSkinException e) {
					safelyLog(Level.SEVERE, new StringBuilder().append("Unable to load '").append(file.getName()).append("' in directory '").append(file.getPath()).append("': ").append(e.getMessage()).toString(), e);
				}
			}
		}

	}

	public Skin loadSkin(File file) throws InvalidSkinException, InvalidDescriptionFileException {
		boolean locked = manager.lock(key);
		Skin result = null;
		
		String ext = Utils.getFileExtention(file.getPath());
		
		if (ext != null && ext.equalsIgnoreCase("jar")) {
			javaLoader.loadSkin(file);
		} else if (ext != null && ext.equalsIgnoreCase("yml")) {
			// TODO Yaml Skin
		}
		

		if (!locked)
			manager.unlock(key);
		
		return result;
	}

	public void enableSkin(Skin skin) {
		if (enabledSkin != null)
			disableSkin(enabledSkin);
		
		if (!skin.isEnabled()) {
			boolean locked = manager.lock(key);

			try {
				skin.getSkinLoader().enableSkin(skin);
				enabledSkin = skin;
			} catch (Exception e) {
				safelyLog(Level.SEVERE, new StringBuilder().append("An error ocurred in the Skin Loader while disabling skin '").append(skin.getDescription().getFullName()).append("': ").append(e.getMessage()).toString(), e);
			}

			if (!locked)
				manager.unlock(key);
		}
	}

	public void disableSkin(Skin skin) {
		if (skin.isEnabled()) {
			boolean locked = manager.lock(key);

			try {
				skin.getSkinLoader().disableSkin(skin);
				enabledSkin = null;
			} catch (Exception e) {
				safelyLog(Level.SEVERE, new StringBuilder().append("An error ocurred in the Skin Loader while disabling skin '").append(skin.getDescription().getFullName()).append("': ").append(e.getMessage()).toString(), e);
			}

			if (!locked)
				manager.unlock(key);
		}
	}

	public void clearSkins() {
		if (getEnabledSkin() != null) {
			disableSkin(getEnabledSkin());
		}

		skins.clear();
		names.clear();
	}

	public Skin getEnabledSkin() {
		return enabledSkin;
	}

	public void addSkin(Skin skin) {
		names.put(skin.getDescription().getName(), skin);
		skins.add(skin);
	}

	private void safelyLog(Level level, String message, Throwable ex) {
		boolean relock = false;
		if (manager.isLocked()) {
			relock = true;
			manager.unlock(key);
		}
		
		Launcher.getLogger().log(level, message, ex);
		
		if (relock) {
			manager.lock(key);
		}
	}

}
