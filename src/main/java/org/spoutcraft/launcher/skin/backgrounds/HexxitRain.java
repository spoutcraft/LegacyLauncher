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

public class HexxitRain extends AnimatedImage {
	private int x = 0;
	private int y = 0;

	public HexxitRain(int x, int startY) {
		super(ResourceUtils.getIcon("rain.png"), 5);
		this.x = x;
		this.y = startY;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		y += 5;

		if (y >= 520) {
			y = -520;
		}

		this.setBounds(x, y, 880, 520);
		this.repaint();
	}
}
