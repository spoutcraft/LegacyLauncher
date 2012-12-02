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
package org.spoutcraft.launcher.skin.components;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.apache.commons.io.IOUtils;
import org.spoutcraft.launcher.util.BlurUtils;
import org.spoutcraft.launcher.util.ResourceUtils;
import org.spoutcraft.launcher.util.Utils;

public class BackgroundImage extends JLabel {
	private static final long serialVersionUID = 1L;

	public BackgroundImage(int width, int height) {
		setVerticalAlignment(SwingConstants.CENTER);
		setHorizontalAlignment(SwingConstants.CENTER);
		setBounds(0, 0, width, height);

		setIcon(new ImageIcon(getBackgroundImage().getScaledInstance(width, height, Image.SCALE_SMOOTH)));
		setVerticalAlignment(SwingConstants.TOP);
		setHorizontalAlignment(SwingConstants.LEFT);
	}

	private BufferedImage getBackgroundImage() {
		final List<File> images = new ArrayList<File>();
		File backgroundDir = new File(new File(Utils.getAssetsDirectory(), "background"), getTimeFolder());
		if (backgroundDir.exists()) {
			for (File f : backgroundDir.listFiles()) {
				if (f.getName().endsWith(".png") || f.getName().endsWith(".jpg")) {
					images.add(f);
				}
			}
		}
		InputStream stream = null;
		BufferedImage image;
		try {
			try {
				stream = new FileInputStream(images.get((new Random()).nextInt(images.size())));
			} catch (Exception io) {
				if (images.size() > 0) {
					io.printStackTrace();
				}
				stream = ResourceUtils.getResourceAsStream("/org/spoutcraft/launcher/resources/background.jpg");
			}
			image = ImageIO.read(stream);
			image = BlurUtils.applyGaussianBlur(image, 10, 1, true);
			return image;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(stream);
		}
		return new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
	}

	private String getTimeFolder() {
		int hours = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		if (hours < 6) {
			return "night";
		}
		if (hours < 12) {
			return "day";
		}
		if (hours < 20) {
			return "evening";
		}
		return "night";
	}
}
