package org.spoutcraft.launcher.api;

import org.spoutcraft.launcher.api.util.Utils;

import java.io.File;

public class SpoutcraftDirectories {

	/* Files */
	private final File binDir = new File(Utils.getWorkingDirectory(), "bin");
	private final File binCacheDir = new File(binDir, "cache");
	private final File updateDir = new File(Utils.getWorkingDirectory(), "temp");
	private final File backupDir = new File(Utils.getWorkingDirectory(), "backups");
	private final File spoutcraftDir = new File(Utils.getWorkingDirectory(), "spoutcraft");
	private final File savesDir = new File(Utils.getWorkingDirectory(), "saves");

	public File getBinDir() {
		return binDir;
	}

	public File getBinCacheDir() {
		return binCacheDir;
	}

	public File getUpdateDir() {
		return updateDir;
	}

	public File getBackupDir() {
		return backupDir;
	}

	public File getSpoutcraftDir() {
		return spoutcraftDir;
	}

	public File getSavesDir() {
		return savesDir;
	}
}
