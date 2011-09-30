/*
 * This file is part of Spoutcraft Launcher (http://wiki.getspout.org/).
 * 
 * Spoutcraft Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spoutcraft Launcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.spoutcraft.launcher.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.commons.io.FileUtils;
import org.spoutcraft.launcher.GameUpdater;
import org.spoutcraft.launcher.Main;
import org.spoutcraft.launcher.PlatformUtils;
import org.spoutcraft.launcher.SettingsHandler;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.Font;
import javax.swing.JComboBox;
import javax.swing.JLabel;

public class OptionDialog extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2453348055512665749L;
	private final JPanel contentPanel = new JPanel();
	public static SettingsHandler settings = new SettingsHandler("defaults/spoutcraft.properties", new File(PlatformUtils.getWorkingDirectory(), "spoutcraft" + File.separator + "spoutcraft.properties"));

	JCheckBox devCheckbox = new JCheckBox("Use latest dev build. Dangerous!");
	
	JCheckBox clipboardCheckbox = new JCheckBox("Allow access to your clipboard");
	
	JCheckBox backupCheckbox = new JCheckBox("Include worlds when doing automated backup");
	
	JCheckBox retryLoginCheckbox = new JCheckBox("Retry after connection timeout");
	
	JCheckBox latestLWJGLCheckbox = new JCheckBox("Use latest LWJGL binaries");
	
	JComboBox memoryCombo = new JComboBox();
	
	JButton clearCache = new JButton("Clear Cache");


	/**
	 * Create the dialog.
	 */
	public OptionDialog() {
		setTitle("Spoutcraft Settings");
		
		settings.load();
		
		System.out.println(settings.getLineCount());
		
		devCheckbox.setToolTipText("Uses the latest development builds of Spoutcraft. They are often unstable!");
		clipboardCheckbox.setToolTipText("Allows server mods to see the contents of your clipboard.");
		backupCheckbox.setToolTipText("Backs up your Spoutcraft SP worlds after each Spoutcraft update");
		retryLoginCheckbox.setToolTipText("Retries logging into minecraft.net up to 3 times after a failure");
		latestLWJGLCheckbox.setToolTipText("Minecraft normally uses older, more compatible versions of LWJGL, but the latest may improve performance or fix audio issues");
		clearCache.setToolTipText("Clears the cached minecraft and spoutcraft files, forcing a redownload on your next login");
		
		if (settings.checkProperty("devupdate")) {
			devCheckbox.setSelected(settings.getPropertyBoolean("devupdate"));
		}
		if (settings.checkProperty("clipboardaccess")) {
			clipboardCheckbox.setSelected(settings.getPropertyBoolean("clipboardaccess"));
		}
		if (settings.checkProperty("worldbackup")) {
			backupCheckbox.setSelected(settings.getPropertyBoolean("worldbackup"));
		}
		if (settings.checkProperty("retryLogins")) {
			retryLoginCheckbox.setSelected(settings.getPropertyBoolean("retryLogins"));
		}
		if (settings.checkProperty("latestLWJGL")) {
			latestLWJGLCheckbox.setSelected(settings.getPropertyBoolean("latestLWJGL"));
		}
		
		setResizable(false);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		memoryCombo.addItem("512 MB");
		memoryCombo.addItem("768 MB");
		memoryCombo.addItem("1 GB");
		memoryCombo.addItem("2 GB");
		memoryCombo.addItem("4 GB");
		
		if (settings.checkProperty("memory")) {
			memoryCombo.setSelectedIndex(settings.getPropertyInteger("memory"));
		}
		
		JLabel lblMemoryToAllocate = new JLabel("Memory to allocate");
		
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(devCheckbox)
						.addComponent(clipboardCheckbox)
						.addComponent(backupCheckbox)
						.addComponent(retryLoginCheckbox)
						.addComponent(latestLWJGLCheckbox)
						.addComponent(clearCache)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(lblMemoryToAllocate)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(memoryCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(27, Short.MAX_VALUE))
		);
		Font font = new Font("Arial", Font.PLAIN, 11);
		backupCheckbox.setFont(font);
		clipboardCheckbox.setFont(font);
		devCheckbox.setFont(font);
		retryLoginCheckbox.setFont(font);
		clearCache.setFont(font);
		clearCache.setActionCommand("Clear Cache");
		clearCache.addActionListener(this);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addComponent(devCheckbox)
					.addComponent(retryLoginCheckbox)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(clipboardCheckbox)
					.addComponent(backupCheckbox)
					.addComponent(latestLWJGLCheckbox)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(memoryCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblMemoryToAllocate))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(clearCache)
					.addContainerGap(316, Short.MAX_VALUE))
		);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setFont(font);
				okButton.setActionCommand("OK");
				okButton.addActionListener(this);
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setFont(font);
				cancelButton.setActionCommand("Cancel");
				cancelButton.addActionListener(this);
				buttonPane.add(cancelButton);
			}
		}
	}

	public void actionPerformed(ActionEvent evt) {
		String btnID = evt.getActionCommand(); 
		if (btnID.equals("OK")) {
			if (settings.checkProperty("devupdate")) {
				settings.changeProperty("devupdate", devCheckbox.isSelected());
			} else {
				settings.put("devupdate", devCheckbox.isSelected());
			}
			if (settings.checkProperty("clipboardaccess")) {
				settings.changeProperty("clipboardaccess", clipboardCheckbox.isSelected());
			} else {
				settings.put("clipboardaccess", clipboardCheckbox.isSelected());
			}
			if (settings.checkProperty("worldbackup")) {
				settings.changeProperty("worldbackup", backupCheckbox.isSelected());
			} else {
				settings.put("worldbackup", backupCheckbox.isSelected());
			}
			if (settings.checkProperty("retryLogins")) {
				settings.changeProperty("retryLogins", retryLoginCheckbox.isSelected());
			} else {
				settings.put("retryLogins", retryLoginCheckbox.isSelected());
			}
			if (settings.checkProperty("memory")) {
				if (settings.getPropertyInteger("memory") != memoryCombo.getSelectedIndex()) {
					settings.changeProperty("memory", memoryCombo.getSelectedIndex());
					int mem = 1 << 8 + OptionDialog.settings.getPropertyInteger("memory");
					Main.reboot("-Xmx" + mem + "m");
				}
			} else {
				settings.put("memory", memoryCombo.getSelectedIndex());
			}
			boolean clearCache = false;
			if (settings.checkProperty("latestLWJGL")) {
				if (settings.getPropertyBoolean("latestLWJGL") != latestLWJGLCheckbox.isSelected()){
					clearCache = true;
				}
				settings.changeProperty("latestLWJGL", latestLWJGLCheckbox.isSelected());
			} else {
				settings.put("latestLWJGL", latestLWJGLCheckbox.isSelected());
				if (latestLWJGLCheckbox.isSelected()) {
					clearCache = true;
				}
			}
			if (clearCache) {
				clearCache();
				
			}
			this.setVisible(false);
			this.dispose();
		} else if (btnID.equals("Cancel")) {
			this.setVisible(false);
			this.dispose();
		}
		else if (btnID.equals("Clear Cache")) {
			clearCache();
		}
	}
	
	private static void clearCache() {
		try {
			FileUtils.deleteDirectory(GameUpdater.binDir);
			FileUtils.deleteDirectory(GameUpdater.updateDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
