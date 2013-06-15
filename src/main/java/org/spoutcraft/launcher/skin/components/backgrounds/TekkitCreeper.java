/*
 * This file is part of Technic Launcher.
 *
 * Copyright (c) 2013-2013, Technic <http://www.technicpack.net/>
 * Technic Launcher is licensed under the Spout License Version 1.
 *
 * Technic Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Technic Launcher is distributed in the hope that it will be useful,
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
package org.spoutcraft.launcher.skin.components.backgrounds;

import org.spoutcraft.launcher.skin.components.AnimatedImage;

import javax.swing.Icon;
import javax.swing.Timer;
import java.awt.event.ActionEvent;

public class TekkitCreeper extends AnimatedImage {
	private static final long serialVersionUID = 1;

	private final int x;
	private final int y;
	private static final int delay = 50;
	private final int distance = 30;
	private int modX = 0;
	private boolean xReverse = false;
	private int modY = 0;
	private boolean yReverse = false;


	public TekkitCreeper(int x, int y, Icon image) {
		super(image, delay);
		this.x = x;
		this.y = y;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (modX == distance) {
			xReverse = true;
		}
		if (modX == 0) {
			xReverse = false;
		}
		if (modY == distance) {
			yReverse = true;
		}
		if (modY == 0) {
			yReverse = false;
		}

		if (xReverse) {
			modX--;
		} else {
			modX++;
		}

		if (yReverse) {
			modY--;
		} else {
			modY++;
		}

		int delayChange = 0;
		if (modX < distance / 2) {
			delayChange = distance - modX - (distance / 2);
		} else {
			delayChange = modX - (distance / 2);
		}
		getTimer().setDelay(delay + (delayChange * 10));

		this.setBounds(x + modX, y + modY, getWidth(), getHeight());
		this.repaint();
	}
}
