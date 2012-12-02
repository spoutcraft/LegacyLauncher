/*
 * This file is part of Spoutcraft.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * Spoutcraft is licensed under the Spout License Version 1.
 *
 * Spoutcraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spoutcraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spoutcraft.launcher.skin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.jdesktop.swingworker.SwingWorker;

import org.spoutcraft.launcher.util.Download;
import org.spoutcraft.launcher.util.ResourceUtils;
import org.spoutcraft.launcher.util.Download.Result;

public class BackgroundImageWorker extends SwingWorker<Object, Object> {
	private static final int IMAGE_CYCLE_TIME = 24 * 60 * 60 * 1000;
	private File backgroundImage;
	private JLabel background;

	public BackgroundImageWorker(File backgroundImage, JLabel background) {
		this.backgroundImage = backgroundImage;
		this.background = background;
	}

	@Override
	protected Object doInBackground() {
		Download download = null;
		try {
			if (!backgroundImage.exists() || backgroundImage.length() < 10 * 1024 || System.currentTimeMillis() - backgroundImage.lastModified() > IMAGE_CYCLE_TIME) {
				download = new Download("http://get.spout.org/splash/random.png", backgroundImage.getPath());
				download.run();
			}
		} catch (Exception e) {
			Logger.getLogger("launcher").log(Level.WARNING, "Failed to download background image", e);
		}
		if (download != null && download.getResult() != Result.SUCCESS) {
			InputStream image = ResourceUtils.getResourceAsStream("/org/spoutcraft/launcher/resources/background.png");
			backgroundImage.delete();
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(backgroundImage);
				fis.getChannel().transferFrom(Channels.newChannel(image), 0, Integer.MAX_VALUE);
			} catch (IOException e) {
				Logger.getLogger("launcher").log(Level.WARNING, "Failed read local background image", e);
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException ignore) { }
				}
				if (image != null) {
					try {
						image.close();
					} catch (IOException ignore) { }
				}
			}
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
