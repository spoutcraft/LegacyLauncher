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

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.apache.commons.io.FileUtils;
import org.spoutcraft.launcher.Settings;
import org.spoutcraft.launcher.api.Launcher;
import org.spoutcraft.launcher.exceptions.RestfulAPIException;
import org.spoutcraft.launcher.skin.MetroLoginFrame;
import org.spoutcraft.launcher.technic.AddPack;
import org.spoutcraft.launcher.technic.CustomInfo;
import org.spoutcraft.launcher.technic.OfflineInfo;
import org.spoutcraft.launcher.technic.PackInfo;
import org.spoutcraft.launcher.technic.PackMap;
import org.spoutcraft.launcher.technic.RestInfo;
import org.spoutcraft.launcher.technic.rest.Modpacks;
import org.spoutcraft.launcher.technic.rest.RestAPI;
import org.spoutcraft.launcher.util.Utils;

public class ModpackSelector extends JComponent implements ActionListener {
	private static final long serialVersionUID = 1L;
	private static final String PACK_SELECT_ACTION = "packselect";
	private ImportOptions importOptions = null;

	private final MetroLoginFrame frame;
	private final PackMap packs = new PackMap();
	private final List<PackButton> buttons = new ArrayList<PackButton>(7);

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

	public ModpackSelector(MetroLoginFrame frame) {
		this.frame = frame;

		for (int i = 0; i < 7; i++) {
			PackButton button = new PackButton();
			buttons.add(button);
			JLabel label = button.getJLabel();
			button.setActionCommand(PACK_SELECT_ACTION);
			button.addActionListener(this);
			if (i == 3) {
				button.setBounds(bigX, bigY, bigWidth, bigHeight);
				label.setBounds(bigX, bigY + bigHeight - 24, bigWidth, 24);
				label.setFont(label.getFont().deriveFont(14F));
				button.setIndex(0);
			} else if (i < 3) {
				int smallX = bigX - ((i + 1) * (smallWidth + spacing));
				button.setBounds(smallX, smallY, smallWidth, smallHeight);
				label.setBounds(smallX, smallY + smallHeight - 20, smallWidth, 20);
				button.setIndex((i + 1) * -1);
			} else if (i > 3) {
				int smallX = bigX + bigWidth + spacing + ((i - 4) * (smallWidth + spacing));
				button.setBounds(smallX, smallY, smallWidth, smallHeight);
				label.setBounds(smallX, smallY + smallHeight - 20, smallWidth, 20);
				button.setIndex(i - 3);
			}

			this.add(button.getJLabel());
			this.add(button);
		}
	}

	public void setupOfflinePacks() {
		if (Settings.getInstalledPacks().isEmpty()) {
			addRestPacks();
		} else {
			initRest();
		}
		initCustom();
		packs.put("addpack", new AddPack());

		selectPack("tekkitlite");
		selectPack(Settings.getLastModpack());
	}

	public void initRest() {
		for (String pack : Settings.getInstalledPacks()) {
			if (Settings.isPackCustom(pack)) {
				continue;
			}
			OfflineInfo info = new OfflineInfo(pack);
			packs.put(pack, info);
		}
	}

	public void initCustom() {
		for (String pack : Settings.getInstalledPacks()) {
			if (!Settings.isPackCustom(pack)) {
				continue;
			}
			OfflineInfo info = new OfflineInfo(pack);
			packs.put(pack, info);
		}
	}

	public void addRestPacks() {
		
		try {
			Modpacks modpacks = new RestAPI(RestAPI.TECHNIC).getModpacks();
			for (String pack : modpacks.getMap().keySet()) {
				try {
					RestInfo info = modpacks.getRest().getModpackInfo(pack);
					packs.add(info);
					if (pack.equals("tekkitlite")) {
						selectPack(info);
					}
				} catch (RestfulAPIException e) {
					Launcher.getLogger().log(Level.SEVERE, "Unable to load modpack " + pack + " from Technic Rest API", e);
				}
				selectPack(packs.getSelected());
			}
		} catch (RestfulAPIException e) {
			Launcher.getLogger().log(Level.SEVERE, "Unable to connect to the Technic Rest API at " + RestAPI.TECHNIC + " Running Offline instead.", e);
		}
	}

	public void addCustomPacks() {
		for (String pack : Settings.getInstalledPacks()) {
			if (!Settings.isPackCustom(pack)) {
				continue;
			}
			try {
				CustomInfo info = RestAPI.getCustomModpack(RestAPI.getCustomPackURL(pack));
				if (info.hasMirror()) {
					RestAPI rest = new RestAPI(info.getMirrorURL());
					RestInfo restInfo = rest.getModpackInfo(pack);
					packs.add(restInfo);
				} else {
					packs.add(info);
				}
			} catch (RestfulAPIException e) {
				Launcher.getLogger().log(Level.SEVERE, "Unable to load modpack " + pack + " from Technic Platform API", e);
			}
			selectPack(packs.getSelected());
		}
	}

	public void addPack(PackInfo pack) {
		packs.addNew(pack);
		selectPack(pack);
	}

	public void removePack() {
		PackInfo pack = packs.get(getSelectedPack().getName());
		
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

		packs.remove(pack.getName());
		selectPack(packs.getPrevious(1));
	}

	public void selectPack(PackInfo pack) {
		selectPack(pack.getName());
	}

	public void selectPack(String name) {
		PackInfo selected = packs.select(name);
		if (selected == null) {
			return;
		}

		// Determine if the pack is custom
		boolean custom = Settings.isPackCustom(selected.getName());

		// Set the background image based on the pack
		frame.getBackgroundImage().setIcon(new ImageIcon(selected.getBackground()));

		// Set the icon image based on the pack
		frame.setIconImage(selected.getIcon());

		// Set the frame title based on the pack
		frame.setTitle(selected.getDisplayName());
		
		// Set the big button image in the middle
		buttons.get(3).setIcon(new ImageIcon(selected.getLogo().getScaledInstance(bigWidth, bigHeight, Image.SCALE_SMOOTH)));
		buttons.get(3).getJLabel().setVisible(selected.isLoading());

		// Set the URL for the platform button
		String url = "http://beta.technicpack.net/modpack/details/" + selected.getName();
		if (selected instanceof RestInfo && !custom) {
			String newUrl = ((RestInfo) selected).getWebURL();
			if (newUrl != null && !newUrl.isEmpty()) {
				url = newUrl;
				frame.enableComponent(frame.getPlatform(), true);
			} else {
				frame.enableComponent(frame.getPlatform(), false);
			}
		}
		frame.getPlatform().setURL(url);

		// Add the first 3 buttons to the left
		for (int i = 0; i < 3; i++) {
			PackInfo pack = packs.getPrevious(i + 1);
			buttons.get(i).setIcon(new ImageIcon(pack.getLogo().getScaledInstance(smallWidth, smallHeight, Image.SCALE_SMOOTH)));
			buttons.get(i).getJLabel().setVisible(pack.isLoading());
		}

		// Add the last 3 buttons to the right
		for (int i = 4; i < 7; i++) {
			PackInfo pack = packs.getNext(i - 3);
			buttons.get(i).setIcon(new ImageIcon(pack.getLogo().getScaledInstance(smallWidth, smallHeight, Image.SCALE_SMOOTH)));
			buttons.get(i).getJLabel().setVisible(pack.isLoading());
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
		selectPack(packs.getNext(1));
	}

	public void selectPreviousPack() {
		selectPack(packs.getPrevious(1));
	}

	public PackInfo getSelectedPack() {
		return packs.getSelected();
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
				selectPack(packs.get(packs.getIndex() + button.getIndex()));
			}
		}
	}
}
