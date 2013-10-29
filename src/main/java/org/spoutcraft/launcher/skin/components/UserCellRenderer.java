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

import net.technicpack.launchercore.install.User;
import net.technicpack.launchercore.util.ImageUtils;
import net.technicpack.launchercore.util.ResourceUtils;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import java.awt.Component;
import java.awt.Font;
import java.io.IOException;
import java.util.HashMap;

public class UserCellRenderer extends JLabel implements ListCellRenderer {
	private Font textFont;
	private Icon backupHeadIcon;
	private Icon addUserIcon;

	private static final int ICON_WIDTH = 32;
	private static final int ICON_HEIGHT = 32;

	private boolean areHeadsReady = false;
	private HashMap<String, Icon> headMap = new HashMap<String, Icon>();

	public UserCellRenderer(Font font) {
		this.textFont = font;
		setOpaque(true);

		try {
			backupHeadIcon = new ImageIcon(ImageUtils.scaleImage(ImageIO.read(ResourceUtils.getResourceAsStream("/org/spoutcraft/launcher/resources/face.png")), ICON_WIDTH, ICON_HEIGHT));
			addUserIcon = new ImageIcon(ImageUtils.scaleImage(ImageIO.read(ResourceUtils.getResourceAsStream("/org/spoutcraft/launcher/resources/add_user.png")), ICON_WIDTH, ICON_HEIGHT));
		} catch (IOException ex) {
			ex.printStackTrace();
			backupHeadIcon = null;
		}
	}

	public void setHeadReady() {
		areHeadsReady = true;
		headMap.clear();
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}

		this.setFont(textFont);

		if (value instanceof User) {
			User user = (User) value;
			this.setText(user.getDisplayName());
			this.setIconTextGap(8);

			if (areHeadsReady) {
				if (!headMap.containsKey(user.getUsername())) {
					headMap.put(user.getUsername(), new ImageIcon(ImageUtils.scaleImage(user.getFaceImage(), ICON_WIDTH, ICON_HEIGHT)));
				}

				Icon head = headMap.get(user.getUsername());

				if (head != null) {
					this.setIcon(head);
				} else if (backupHeadIcon != null) {
					this.setIcon(backupHeadIcon);
				}
			} else if (backupHeadIcon != null) {
				this.setIcon(backupHeadIcon);
			}
		} else if (value == null) {
			this.setText("Add New User");
			this.setIconTextGap(8);

			if (addUserIcon != null) {
				this.setIcon(addUserIcon);
			}
		} else {
			this.setIconTextGap(0);
			this.setText(value.toString());
		}

		return this;
	}
}
