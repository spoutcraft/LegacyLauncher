package org.spoutcraft.launcher.technic;

import java.io.File;
import java.io.IOException;

import org.spoutcraft.launcher.api.Launcher;
import org.spoutcraft.launcher.exceptions.DownloadException;
import org.spoutcraft.launcher.util.DownloadListener;
import org.spoutcraft.launcher.util.DownloadUtils;

public class TechnicModpack extends Modpack {

	public static void checkForUpdates(String modpack, DownloadListener listener) throws DownloadException {
		File location = new File(Launcher.getGameUpdater().getTempDir(), "modpack.yml");
		try {
			DownloadUtils.downloadFile(TechnicRestAPI.getModpackYMLURL(modpack), location.getPath(), location.getName(), TechnicRestAPI.getModpackMD5(modpack), listener);
		} catch (IOException e) {
			throw new DownloadException(e);
		}
	}
}
