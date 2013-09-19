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
import org.spoutcraft.launcher.skin.LauncherFrame;

import java.awt.Color;
import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class PackButton extends ImageButton {
	private static final long serialVersionUID = 1L;
	private int index;
	private JLabel label;

	public PackButton() {
		super();
		label = new JLabel("Loading...");
		label.setFont(LauncherFrame.getMinecraftFont(12));
		label.setForeground(Color.WHITE);
		label.setBackground(new Color(35, 35, 35));
		label.setOpaque(true);
		label.setHorizontalAlignment(CENTER);
	}

	public void setPack(InstalledPack pack) {
		setIcon(new ImageIcon(pack.getLogo().getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH)));
		label.setVisible(pack.getInfo() == null && !(pack instanceof AddPack));
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

	public void setIndex(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}
}
