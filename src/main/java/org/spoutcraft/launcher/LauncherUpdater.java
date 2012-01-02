package org.spoutcraft.launcher;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.spoutcraft.launcher.async.DownloadListener;

public class LauncherUpdater implements DownloadListener {

	public static String getCurrentBuild() {
		try {
			return LauncherUpdater.readInputStreamAsString(LauncherUpdater.class.getResource("version").openStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void stateChanged(String fileName, float progress) {

	}

	public static String readInputStreamAsString(InputStream in) throws IOException {

		BufferedInputStream bis = new BufferedInputStream(in);
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		int result = bis.read();
		while (result != -1) {
			byte b = (byte) result;
			buf.write(b);
			result = bis.read();
		}
		return buf.toString();
	}

}
