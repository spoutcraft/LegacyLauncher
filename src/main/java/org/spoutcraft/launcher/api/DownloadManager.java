package org.spoutcraft.launcher.api;

import java.net.MalformedURLException;

import org.spoutcraft.launcher.api.skin.exceptions.SkinSecurityException;
import org.spoutcraft.launcher.api.util.Download;
import org.spoutcraft.launcher.api.util.Utils;

public class DownloadManager {
	public final double key;
	public final String[] safeExtensions = {"txt", "yml", "xml", "properties", "png", "jpg", "gif", "bmp"};
	
	public DownloadManager(final double key) {
		this.key = key;
	}
	
	public Download getDownload(String url, String out) throws MalformedURLException {
		String ext = Utils.getFileExtention(out);
		if (!isSafe(ext)) {
			throw new SkinSecurityException(new StringBuilder().append("The Skin '").append(Launcher.getSkinManager().getEnabledSkin().getDescription().getName()).append("' tried to download a file that could be harmful.").toString());
		}
		
		return new Download(url, out);
	}
	
	public Download getUnrestrictedDownload(String url, String out, double key) throws MalformedURLException {
		if (key != this.key) 
			throw new SkinSecurityException(new StringBuilder().append("The Skin '").append(Launcher.getSkinManager().getEnabledSkin().getDescription().getName()).append("' used the wrong key to unlock the DownloadManager.").toString());
		return new Download(url, out);
	}
	
	public boolean isSafe(String ext) {
		for (String safe : safeExtensions) {
			if (ext.equalsIgnoreCase(safe)) {
				return true;
			}
		}
		return false;
	}
}
