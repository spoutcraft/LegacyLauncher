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
import javax.swing.ComboBoxEditor;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class UserCellEditor implements ComboBoxEditor, DocumentListener {
	private Font textFont;
	private Icon backupHeadIcon;

	private static final int ICON_WIDTH=32;
	private static final int ICON_HEIGHT=32;

	private JPanel parentPanel;
	private JLabel userLabel;
	private JTextField textField;
	private CardLayout layout;

	private Object currentObject;
	private boolean areHeadsReady = false;
	private HashMap<String, Icon> headMap = new HashMap<String, Icon>();

	Collection<ActionListener> actionListeners = new HashSet<ActionListener>();

	private static final String USER = "user";
	private static final String STRING = "string";

	public UserCellEditor(Font font) {
		this.textFont = font;

		layout = new CardLayout();

		parentPanel = new JPanel();
		parentPanel.setLayout(layout);

		userLabel = new JLabel();
		userLabel.setOpaque(true);
		userLabel.setFont(textFont);
		userLabel.setBackground(Color.white);
		parentPanel.add(userLabel, USER);

		textField = new JTextField();
		textField.setOpaque(true);
		textField.setFont(textFont);
		textField.setBackground(Color.white);
		textField.setBorder(null);
		textField.getDocument().addDocumentListener(this);
		parentPanel.add(textField, STRING);

		try {
			backupHeadIcon = new ImageIcon(ImageUtils.scaleImage(ImageIO.read(ResourceUtils.getResourceAsStream("/org/spoutcraft/launcher/resources/face.png")), ICON_WIDTH, ICON_HEIGHT));
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
	public Component getEditorComponent() {
		return parentPanel;
	}

	@Override
	public void setItem(Object anObject) {
		currentObject = anObject;

		if (anObject instanceof User) {
			User user = (User)anObject;
			userLabel.setText(user.getDisplayName());
			userLabel.setIconTextGap(8);

			if (areHeadsReady) {
				if (!headMap.containsKey(user.getUsername())) {
					headMap.put(user.getUsername(), new ImageIcon(ImageUtils.scaleImage(user.getFaceImage(), ICON_WIDTH, ICON_HEIGHT)));
				}

				Icon head = headMap.get(user.getUsername());

				if (head != null) {
					userLabel.setIcon(head);
				} else if (backupHeadIcon != null) {
					userLabel.setIcon(backupHeadIcon);
				}
			} else if (backupHeadIcon != null) {
				userLabel.setIcon(backupHeadIcon);
			}

			layout.show(parentPanel, USER);
		} else {
			String newText = "";

			if (anObject != null) {
				newText = anObject.toString();
			}

			if (!textField.getText().equals(newText))
				textField.setText(newText);

			layout.show(parentPanel, STRING);
			textField.requestFocus();
		}
	}

	@Override
	public Object getItem() {
		return currentObject;
	}

	@Override
	public void selectAll() {
		return;
	}

	@Override
	public void addActionListener(ActionListener l) {
		actionListeners.add(l);
	}

	@Override
	public void removeActionListener(ActionListener l) {
		actionListeners.remove(l);
	}

	public void addKeyListener(KeyListener k) {
		userLabel.addKeyListener(k);
	}

	public void removeKeyListener(KeyListener k) {
		userLabel.removeKeyListener(k);
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		currentObject = textField.getText();
		for(ActionListener listener : actionListeners) {
			listener.actionPerformed(new ActionEvent(this, 0, "edited"));
		}
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		currentObject = textField.getText();
		for(ActionListener listener : actionListeners) {
			listener.actionPerformed(new ActionEvent(this, 0, "edited"));
		}
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		currentObject = textField.getText();
		for(ActionListener listener : actionListeners) {
			listener.actionPerformed(new ActionEvent(this, 0, "edited"));
		}
	}
}
