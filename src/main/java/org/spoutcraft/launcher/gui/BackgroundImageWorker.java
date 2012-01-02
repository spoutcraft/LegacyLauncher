package org.spoutcraft.launcher.gui;

import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.jdesktop.swingworker.SwingWorker;
import org.spoutcraft.launcher.async.Download;

public class BackgroundImageWorker extends SwingWorker<Object, Object>{
	private static final int IMAGE_CYCLE_TIME = 24 * 60 * 60 * 1000;
	private File backgroundImage;
	private JLabel background;
	public BackgroundImageWorker(File backgroundImage, JLabel background) {
		this.backgroundImage = backgroundImage;
		this.background = background;
	}

	@Override
	protected Object doInBackground() {
		try {
			if (!backgroundImage.exists() || backgroundImage.length() < 10*1024 || System.currentTimeMillis() - backgroundImage.lastModified() > IMAGE_CYCLE_TIME) {
				Download download = new Download("http://dl.getspout.org/splash/index.php", backgroundImage.getPath());
				download.run();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void done() {
		background.setIcon(new ImageIcon(backgroundImage.getPath()));
		background.setVerticalAlignment(SwingConstants.TOP);
		background.setHorizontalAlignment(SwingConstants.LEFT);
	}

}
