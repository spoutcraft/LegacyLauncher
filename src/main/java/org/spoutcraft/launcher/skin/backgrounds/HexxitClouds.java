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
package org.spoutcraft.launcher.skin.backgrounds;

import net.technicpack.launchercore.util.ResourceUtils;
import org.spoutcraft.launcher.skin.components.AnimatedImage;

import java.awt.event.ActionEvent;

public class HexxitClouds extends AnimatedImage {
	private int x = 0;
	private int y = 0;

	public HexxitClouds(int startX, int y, String image, int delay) {
		super(ResourceUtils.getIcon(image), delay);
		this.x = startX;
		this.y = y;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		x -= 1;

		if (x <= -880) {
			x = 880;
		}

		this.setBounds(x, y, 880, 520);
		this.repaint();
	}
}
