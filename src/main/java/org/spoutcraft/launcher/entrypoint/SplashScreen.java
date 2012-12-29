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
package org.spoutcraft.launcher.entrypoint;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JWindow;

public class SplashScreen extends JWindow{
	private static final long serialVersionUID = 1L;
	protected final ImageIcon icon;

	public SplashScreen(Image image) {
		this.icon = new ImageIcon(image);

		Container container = getContentPane();
		container.setLayout(null);

		// Redraw the image to fix the alpha channel
		BufferedImage alphaImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = alphaImage.createGraphics();
		g.drawImage(image, 0, 0, icon.getIconWidth(), icon.getIconHeight(), null);
		g.dispose();

		// Draw the image
		JButton background = new JButton(new ImageIcon(alphaImage));
		background.setBounds(0, 0, icon.getIconWidth(), icon.getIconHeight());
		background.setRolloverEnabled(true);
		background.setRolloverIcon(background.getIcon());
		background.setSelectedIcon(background.getIcon());
		background.setDisabledIcon(background.getIcon());
		background.setPressedIcon(background.getIcon());
		background.setFocusable(false);
		background.setContentAreaFilled(false);
		background.setBorderPainted(false);
		background.setOpaque(false);
		container.add(background);

		// Finalize
		setSize(icon.getIconWidth(), icon.getIconHeight() + 20);
		try {
			//Not always supported...
			this.setBackground(new Color(0, 0, 0, 0));
		} catch (UnsupportedOperationException e) {
			this.setBackground(new Color(0, 0, 0));
		}
		setLocationRelativeTo(null);
	}
}
