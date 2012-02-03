package org.spoutcraft.launcher.api.skin;

import java.io.File;

public abstract class JavaSkin implements Skin {
	private SkinDescriptionFile desc = null;
	private File file;
	private File dataFolder;
	private boolean enabled;
	private JavaSkinLoader loader;
	private CommonClassLoader classLoader;
	
	
	public final SkinDescriptionFile getDescription() {
		return desc;
	}

	public final File getDataFolder() {
		return dataFolder;
	}

	public final void initialize(JavaSkinLoader loader, SkinDescriptionFile desc, File dataFolder, File file, CommonClassLoader classLoader) {
		this.loader = loader;
		this.desc = desc;
		this.dataFolder = dataFolder;
		this.file = file;
		this.classLoader = classLoader;
		
	}

	protected final CommonClassLoader getClassLoader() {
		return classLoader;
	}

	public final void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public final boolean isEnabled() {
		return enabled;
	}
	public final SkinLoader getSkinLoader() {
		return loader;
	}
	
	public final File getFile() {
		return file;
	}
}
