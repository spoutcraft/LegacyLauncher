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

package org.spoutcraft.launcher.technic.skin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.JComponent;

import org.apache.commons.io.FileUtils;
import org.spoutcraft.launcher.Settings;
import org.spoutcraft.launcher.exceptions.RestfulAPIException;
import org.spoutcraft.launcher.skin.MetroLoginFrame;
import org.spoutcraft.launcher.technic.AddPack;
import org.spoutcraft.launcher.technic.InstalledOffline;
import org.spoutcraft.launcher.technic.InstalledPack;
import org.spoutcraft.launcher.technic.InstalledRest;
import org.spoutcraft.launcher.technic.rest.RestAPI;
import org.spoutcraft.launcher.technic.rest.info.CustomInfo;
import org.spoutcraft.launcher.technic.rest.info.OfflineInfo;
import org.spoutcraft.launcher.technic.rest.info.RestInfo;
import org.spoutcraft.launcher.util.Utils;

public class ModpackSelector extends JComponent implements ActionListener {
	private static final long serialVersionUID = 1L;
	private static final String PACK_SELECT_ACTION = "packselect";
	private ImportOptions importOptions = null;

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
		this.index = -1;

		for (int i = 0; i < 7; i++) {
			PackButton button = new PackButton();
			buttons.add(button);
			button.setActionCommand(PACK_SELECT_ACTION);
			button.addActionListener(this);
			if (i == 3) {
				button.setBounds(bigX, bigY, bigWidth, bigHeight);
				button.setIndex(0);
			} else if (i < 3) {
				int smallX = bigX - ((i + 1) * (smallWidth + spacing));
				button.setBounds(smallX, smallY, smallWidth, smallHeight);
				button.setIndex((i + 1) * -1);
			} else if (i > 3) {
				int smallX = bigX + bigWidth + spacing + ((i - 4) * (smallWidth + spacing));
				button.setBounds(smallX, smallY, smallWidth, smallHeight);
				button.setIndex(i - 3);
			}
			
			this.add(button);
		}
	}

	public void setupModpackButtons() throws IOException {
		List<RestInfo> modpacks = RestAPI.TECHNIC.getRestInfos();
		for (RestInfo info : modpacks) {
			installedPacks.add(new InstalledRest(info));
		}
		if (Settings.getInstalledPacks() != null) {
			for (String pack : Settings.getInstalledPacks()) {
				if (Settings.isPackCustom(pack)) {
					try {
						CustomInfo info = RestAPI.getCustomModpack(RestAPI.getCustomPackURL(pack));
						installedPacks.add(info.getPack());
					} catch (RestfulAPIException e) {
						e.printStackTrace();
						String build = Settings.getModpackBuild(pack);
						if (build != null) {
							installedPacks.add(new InstalledOffline(new OfflineInfo(pack, build)));
						}
					}
				}
			}
		}
		installedPacks.add(new AddPack());
		
		String lastPack = Settings.getLastModpack();
		if (lastPack == null) {
			selectPack(0);
		} else {
			selectPack(lastPack);
		}
	}

	public void addPack(InstalledPack pack) {
		int loc = installedPacks.size() - 1;
		installedPacks.add(loc, pack);
		selectPack(loc);
	}

	public void removePack() {
		boolean custom = Settings.isPackCustom(getSelectedPack().getName());
		if (custom) {
			InstalledPack pack = installedPacks.remove(getIndex());
			
			Settings.removePack(pack.getName());
			Settings.getYAML().save();
			File file = pack.getPackDirectory();
			if (file.exists()) {
				try {
					FileUtils.deleteDirectory(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			file = new File(Utils.getAssetsDirectory(), pack.getName());
			if (file.exists()) {
				try {
					FileUtils.deleteDirectory(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			selectPack(0);
		}
	}

	public int getIndex() {
		return this.index;
	}

	public void selectPack(String pack) {
		for (int i = 0; i < installedPacks.size(); i++) {
			InstalledPack installed = installedPacks.get(i);
			if (installed.getName().equals(pack)) {
				selectPack(i);
			}
		}
	}

	public void selectPack(int index) {
		if (index >= installedPacks.size()) {
			selectPack(index - installedPacks.size());
		} else if (index < 0) {
			selectPack(installedPacks.size() + index);
		} else {
			this.index = index;
		}

		InstalledPack selected = installedPacks.get(getIndex());

		// Determine if the pack is custom
		boolean custom = Settings.isPackCustom(selected.getName());

		// Set the background image based on the pack
		frame.getBackgroundImage().setIcon(selected.getBackground());

		// Set the icon image based on the pack
		frame.setIconImage(selected.getIcon());

		// Set the frame title based on the pack
		frame.setTitle(selected.getDisplayName());
		
		// Set the big button image in the middle
		buttons.get(3).setIcon(selected.getLogo(bigWidth, bigHeight));

		// Set the URL for the platform button
		String url = "http://beta.technicpack.net/modpack/details/" + selected.getName();
		if (selected instanceof InstalledRest && !custom) {
			String newUrl = ((InstalledRest) selected).getWebURL();
			if (newUrl != null && !newUrl.isEmpty()) {
				url = newUrl;
				frame.enableComponent(frame.getPlatform(), true);
			} else {
				frame.enableComponent(frame.getPlatform(), false);
			}
		}
		frame.getPlatform().setURL(url);

		// Start the iterator at the selected pack
		ListIterator<InstalledPack> iterator = installedPacks.listIterator(getIndex());
		// Add the first 3 buttons to the left
		for (int i = 0; i < 3; i++) {
			// If you run out of packs, start the iterator back at the last element
			if (!iterator.hasPrevious()) {
				iterator = installedPacks.listIterator(installedPacks.size());
			}
			InstalledPack pack = iterator.previous();
			buttons.get(i).setIcon(pack.getLogo(smallWidth, smallHeight));
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
			buttons.get(i).setIcon(pack.getLogo(smallWidth, smallHeight));
		}

		if (selected instanceof AddPack) {
			frame.enableComponent(frame.getPackOptionsBtn(), false);
			frame.enableComponent(frame.getPackRemoveBtn(), false);
			frame.enableComponent(frame.getCustomName(), false);
			frame.enableComponent(frame.getLoginButton(), false);
			frame.enableComponent(frame.getPlatform(), false);
		} else if (custom) {
			if (selected.getLogoURL().equals("")) {
				frame.setCustomName(selected.getDisplayName());
				frame.enableComponent(frame.getCustomName(), true);
			} else {
				frame.enableComponent(frame.getCustomName(), false);
			}
			frame.enableComponent(frame.getPackOptionsBtn(), true);
			frame.enableComponent(frame.getPackRemoveBtn(), true);
			frame.enableComponent(frame.getLoginButton(), true);
			frame.enableComponent(frame.getPlatform(), true);
		} else {
			frame.enableComponent(frame.getPackOptionsBtn(), true);
			frame.enableComponent(frame.getPackRemoveBtn(), false);
			frame.enableComponent(frame.getCustomName(), false);
			frame.enableComponent(frame.getLoginButton(), true);
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

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JComponent) {
			action(e.getActionCommand(), (JComponent)e.getSource());
		}
	}

	public void action(String action, JComponent c) {
		if (action.equals(PACK_SELECT_ACTION) && c instanceof PackButton) {
			PackButton button = (PackButton) c;
			
			if (button.getIndex() == 0 && getSelectedPack() instanceof AddPack) {
				if (importOptions == null || !importOptions.isVisible()) {
					importOptions = new ImportOptions();
					importOptions.setModal(true);
					importOptions.setVisible(true);
				}
			} else {
				selectPack(getIndex() + button.getIndex()); 
			}
		}
	}
}
