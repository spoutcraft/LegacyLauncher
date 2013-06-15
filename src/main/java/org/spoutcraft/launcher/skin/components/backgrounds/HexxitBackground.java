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

import org.spoutcraft.launcher.skin.TechnicLoginFrame;
import org.spoutcraft.launcher.skin.components.EnhancedBackground;

import javax.swing.JLabel;

public class HexxitBackground extends EnhancedBackground {
	private JLabel foreground;
	private HexxitClouds firstBackgroundClouds;
	private HexxitClouds secondBackgroundClouds;
	private HexxitClouds firstForegroundClouds;
	private HexxitClouds secondForegroundClouds;
	private HexxitFlash flash;
	private HexxitRain firstRain;
	private HexxitRain secondRain;

	public HexxitBackground() {
		super("hexxit");
		foreground = new JLabel();
		foreground.setBounds(0, 0, 880, 520);
		foreground.setIcon(TechnicLoginFrame.getIcon("foreground.png"));
		firstBackgroundClouds = new HexxitClouds(0, -50, "clouds.png", 100);
		secondBackgroundClouds = new HexxitClouds(880, -50, "clouds.png", 100);
		firstForegroundClouds = new HexxitClouds(0, -200, "foregroundClouds.png", 50);
		secondForegroundClouds = new HexxitClouds(880, -200, "foregroundClouds.png", 50);
		flash = new HexxitFlash();
		firstRain = new HexxitRain(0, 0);
		secondRain = new HexxitRain(0, 520);

		this.add(firstForegroundClouds);
		this.add(secondForegroundClouds);
		this.add(firstRain);
		this.add(secondRain);
		this.add(foreground);
		this.add(firstBackgroundClouds);
		this.add(secondBackgroundClouds);
		this.add(flash);
	}

	@Override
	public void setVisible(boolean aFlag) {
		firstBackgroundClouds.setAnimating(aFlag);
		secondBackgroundClouds.setAnimating(aFlag);
		firstForegroundClouds.setAnimating(aFlag);
		secondForegroundClouds.setAnimating(aFlag);
		firstRain.setAnimating(aFlag);
		secondRain.setAnimating(aFlag);
		flash.setAnimating(aFlag);
		super.setVisible(aFlag);
	}
}
