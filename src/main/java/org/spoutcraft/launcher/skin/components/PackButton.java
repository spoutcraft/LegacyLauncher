/*
 * This file is part of Technic Launcher.
 * Copyright (C) 2013 Syndicate, LLC
 *
 * Technic Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Technic Launcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Technic Launcher.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.spoutcraft.launcher.skin.components;

import net.technicpack.launchercore.install.AddPack;
import net.technicpack.launchercore.install.InstalledPack;
import net.technicpack.launchercore.util.ResourceUtils;
import org.spoutcraft.launcher.skin.LauncherFrame;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class PackButton extends ImageButton {
	private static final long serialVersionUID = 1L;
	private int index;
	private JLabel label;

	private BufferedImage disconnectedImage;
	private ImageIcon disconnectedIcon;
	private JLabel disconnectedLabel;

	public PackButton() {
		super();
		label = new JLabel("Loading...");
		label.setFont(LauncherFrame.getMinecraftFont(12));
		label.setForeground(Color.WHITE);
		label.setBackground(new Color(35, 35, 35));
		label.setOpaque(true);
		label.setHorizontalAlignment(CENTER);

		try {
			disconnectedImage = ImageIO.read(ResourceUtils.getResourceAsStream("/org/spoutcraft/launcher/resources/offlinePack.png"));
			disconnectedIcon = new ImageIcon(disconnectedImage.getScaledInstance(80,17,Image.SCALE_SMOOTH));
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		if (disconnectedIcon != null)
		{
			disconnectedLabel = new JLabel("");
			disconnectedLabel.setHorizontalAlignment(RIGHT);
			disconnectedLabel.setIcon(disconnectedIcon);
			disconnectedLabel.setVisible(false);
			this.add(disconnectedLabel);
		}
	}

	public void setPack(InstalledPack pack) {
		setIcon(new ImageIcon(pack.getLogo().getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH)));

		if (pack.isLocalOnly() || pack.getInfo() != null)
		{
			label.setText(pack.getDisplayName());
			label.setVisible(!pack.hasLogo());
			disconnectedLabel.setVisible(pack.isLocalOnly());
		} else
		{
			label.setVisible((pack.getInfo() == null || !pack.hasLogo()) && !(pack instanceof AddPack));
			disconnectedLabel.setVisible(false);
		}
	}

	@Override
	public void setIcon(Icon defaultIcon) {
		super.setIcon(defaultIcon);
		if (index == 0) {
			this.setSelectedIcon(defaultIcon);
			this.setRolloverIcon(defaultIcon);
			this.setPressedIcon(defaultIcon);
		}
	}

	public JLabel getJLabel() {
		return label;
	}

	public JLabel getDisconnectedIcon() {
		return disconnectedLabel;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}
}
