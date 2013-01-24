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
package org.spoutcraft.launcher.technic.skin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.JComponent;

import org.spoutcraft.launcher.skin.MetroLoginFrame;
import org.spoutcraft.launcher.technic.InstalledPack;
import org.spoutcraft.launcher.technic.ModpackInfo;
import org.spoutcraft.launcher.technic.TechnicRestAPI;

public class ModpackSelector extends JComponent {
	private static final long serialVersionUID = 1L;

	private final MetroLoginFrame frame;
	private List<InstalledPack> installedPacks = new ArrayList<InstalledPack>();
	private List<PackButton> buttons = new ArrayList<PackButton>(7);

	private final int height = 170;
	private final int width = 880;
	private final int bigWidth = 180;
	private final int bigHeight = 110;
	private final float smallScale = 0.7F;
	private final int spacing = 15;
	private final int smallWidth = (int) (bigWidth * smallScale);
	private final int smallHeight = (int) (bigHeight * smallScale);
	private final int bigX = (width / 2) - (bigWidth / 2);
	private final int bigY = (height / 2) - (bigHeight / 2);
	private final int smallY = (height / 2) - (smallHeight / 2);

	private int index;

	public ModpackSelector(MetroLoginFrame frame) {
		this.frame = frame;
		this.index = 0;

		for (int i = 0; i < 7; i++) {
			PackButton button = new PackButton(i);
			buttons.add(button);
			
			if (i == 3) {
				button.setBounds(bigX, bigY, bigWidth, bigHeight);
			} else if (i < 3) {
				int smallX = bigX - ((i + 1)* (smallWidth + spacing));
				button.setBounds(smallX, smallY, smallWidth, smallHeight);
			} else if (i > 3) {
				int smallX = bigX + bigWidth + spacing + ((i - 4) * (smallWidth + spacing));
				button.setBounds(smallX, smallY, smallWidth, smallHeight);
			}
			
			this.add(button);
		}
	}

	public void setupModpackButtons() throws IOException {
		List<ModpackInfo> modpacks = TechnicRestAPI.getModpacks();
		for (ModpackInfo info : modpacks) {
			installedPacks.add(new InstalledPack(info));
		}
		selectPack(0);
	}

	private int getIndex() {
		return this.index;
	}

	public void selectPack(int index) {
		if (index >= installedPacks.size()) {
			this.index = 0;
		} else if (index < 0) {
			this.index = installedPacks.size() - 1;
		} else {
			this.index = index;
		}

		// Set the background image based on the pack
		frame.getBackgroundImage().setIcon(installedPacks.get(getIndex()).getBackground());

		// Set the icon image based on the pack
		frame.setIconImage(installedPacks.get(getIndex()).getIcon());

		// Set the big button image in the middle
		buttons.get(3).setIcon(installedPacks.get(getIndex()).getImage(bigWidth, bigHeight));

		// Start the iterator at the selected pack
		ListIterator<InstalledPack> iterator = installedPacks.listIterator(getIndex());
		// Add the first 3 buttons to the left
		for (int i = 0; i < 3; i++) {
			// If you run out of packs, start the iterator back at the last element
			if (!iterator.hasPrevious()) {
				iterator = installedPacks.listIterator(installedPacks.size());
			}
			InstalledPack pack = iterator.previous();
			buttons.get(i).setIcon(pack.getImage(smallWidth, smallHeight));
		}

		// Start the iterator just after the selected pack
		iterator = installedPacks.listIterator(getIndex() + 1);
		// Add the last 3 buttons to the right
		for (int i = 4; i < 7; i++) {
			// If you run out of packs, start the iterator back at 0
			if (!iterator.hasNext()) {
				iterator = installedPacks.listIterator(0);
			}
			InstalledPack pack = iterator.next();
			buttons.get(i).setIcon(pack.getImage(smallWidth, smallHeight));
		}

		this.repaint();
	}

	public void selectNextPack() {
		selectPack(getIndex() + 1);
	}

	public void selectPreviousPack() {
		selectPack(getIndex() - 1);
	}

	public InstalledPack getSelectedPack() {
		return installedPacks.get(index);
	}
	
}
