package org.spoutcraft.launcher.api;

import java.util.Random;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

import org.spoutcraft.launcher.api.security.CommonSecurityManager;
import org.spoutcraft.launcher.api.skin.CommonSkinManager;
import org.spoutcraft.launcher.api.skin.GameLauncher;
import org.spoutcraft.launcher.api.skin.SkinManager;

public class Launcher {
	private static Launcher instance;
	private final Logger logger = Logger.getLogger("org.spoutcraft.launcher.Main");
	private final SkinManager skinManager;
	private final double key =(new Random()).nextDouble();
	private final CommonSecurityManager security = new CommonSecurityManager(key);
	private final DownloadManager downloads = new DownloadManager(key);
	private final GameUpdater updater;
	private final GameLauncher launcher;
	
	public Launcher(final GameUpdater updater, final GameLauncher launcher) {
		if (Launcher.instance != null)
			throw new IllegalArgumentException("You can have a duplicate Launcher");
		this.updater = updater;
		this.launcher = launcher;
		logger.addHandler(new ConsoleHandler());
		
		System.setSecurityManager(security);
		
		skinManager = new CommonSkinManager(security, key);
		instance = this;
	}
	
	public static GameUpdater getGameUpdater() {
		if (instance == null) {
			System.out.println("instance is null");
		}
		if (instance.updater == null) {
			System.out.println("updater is null");
		}
		return instance.updater;
	}

	public static Logger getLogger() {
		return instance.logger;
	}

	public static SkinManager getSkinManager() {
		return instance.skinManager;
	}

	public static DownloadManager getDownloadManager() {
		return instance.downloads;
	}

	public static GameLauncher getGameLauncher() {
		return instance.launcher;
	}

	public static void clearCache() {

	}

}
