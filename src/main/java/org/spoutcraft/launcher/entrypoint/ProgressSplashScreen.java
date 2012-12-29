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

import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JProgressBar;

public class ProgressSplashScreen extends SplashScreen{
	private static final long serialVersionUID = 1L;
	private JProgressBar progressBar = new JProgressBar();

	public ProgressSplashScreen() {
		super(Toolkit.getDefaultToolkit().getImage(SplashScreen.class.getResource("/org/spoutcraft/launcher/resources/splash.png")));

		// Setup the progress bar
		progressBar.setFont(new Font("Arial", Font.PLAIN, 11));
		progressBar.setMaximum(100);
		progressBar.setBounds(0, icon.getIconHeight(), icon.getIconWidth(), 20);
		progressBar.setString("Downloading launcher updates...");
		getContentPane().add(progressBar);
		setVisible(true);
	}

	public void updateProgress(int percent) {
		if (percent >= 0 && percent <= 100) {
			progressBar.setValue(percent);
		}
	}
}
