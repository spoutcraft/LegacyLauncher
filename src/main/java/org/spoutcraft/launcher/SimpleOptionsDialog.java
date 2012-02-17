/*
 * This file is part of Launcher (http://www.spout.org/).
 *
 * Launcher is licensed under the SpoutDev License Version 1.
 *
 * Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Launcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */

package org.spoutcraft.launcher;

import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
import org.spoutcraft.launcher.api.Build;
import org.spoutcraft.launcher.api.Launcher;
import org.spoutcraft.launcher.api.skin.gui.Alignment;
import org.spoutcraft.launcher.util.MinecraftDownloadUtils;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SimpleOptionsDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();

	JRadioButton devBuilds = new JRadioButton("Always use development builds");
	JRadioButton recBuilds = new JRadioButton("Always use recommended builds");
	JRadioButton customBuilds = new JRadioButton("Manual build selection");
	JCheckBox clipboardCheckbox = new JCheckBox("Allow access to your clipboard");
	JCheckBox retryLoginCheckbox = new JCheckBox("Retry after connection timeout");
	JCheckBox latestLWJGLCheckbox = new JCheckBox("Use latest LWJGL binaries");
	JCheckBox alwaysUpdateCheckbox = new JCheckBox("Always install updates");
	JComboBox memoryCombo = new JComboBox();
	JButton clearCache = new JButton("Clear Cache");
	JLabel buildInfo = new JLabel();
	JComboBox buildsCombo = new JComboBox();
	int MINIMUM_FAST_LOGIN_BUILD = 905;
	String fastLoginTooltip;

	/**
	 * Create the dialog.
	 */
	public SimpleOptionsDialog() {
		super(Launcher.getSkinManager().getEnabledSkin().getLoginFrame());
		setTitle("Spoutcraft Options");
		this.setAlwaysOnTop(true);
		ButtonGroup group = new ButtonGroup();
		group.add(devBuilds);
		group.add(recBuilds);
		group.add(customBuilds);

		buildInfo.setText("Spoutcraft Launcher Build " + Main.getBuild("launcher-version"));
		buildInfo.setOpaque(true);
		buildInfo.setForeground(Color.DARK_GRAY);
		buildInfo.setToolTipText("Created by the Spout Development Team. Licensed under the LGPL. Source code is available at www.github.com/SpoutDev");
		alwaysUpdateCheckbox.setToolTipText("Automatically accept all updates instead of prompting you. Use at your own risk!");
		customBuilds.setToolTipText("Only use if you know what you are doing!");
		devBuilds.setToolTipText("Development builds are often unstable and buggy. Use at your own risk!");
		recBuilds.setToolTipText("Recommended builds are (nearly) bug-free and well-tested.");
		clipboardCheckbox.setToolTipText("Allows server mods to see the contents of your clipboard.");
		retryLoginCheckbox.setToolTipText("Retries logging into minecraft.net up to 3 times after a failure");
		latestLWJGLCheckbox.setToolTipText("Minecraft normally uses older, more compatible versions of LWJGL, but the latest may improve performance or fix audio issues");
		clearCache.setToolTipText("Clears the cached minecraft and spoutcraft files, forcing a redownload on your next login");
		memoryCombo.setToolTipText("Allows you to adjust the memory assigned to Spoutcraft. Assigning more memory than you have may cause crashes.");

		if (Settings.getLauncherBuild() == Build.RECOMMENDED) {
			devBuilds.setSelected(false);
			recBuilds.setSelected(true);
			customBuilds.setSelected(false);
		} else if (Settings.getLauncherBuild() == Build.DEV) {
			devBuilds.setSelected(true);
			recBuilds.setSelected(false);
			customBuilds.setSelected(false);
		} else {
			devBuilds.setSelected(false);
			recBuilds.setSelected(false);
			customBuilds.setSelected(true);
		}
		customBuilds.addActionListener(this);
		recBuilds.addActionListener(this);
		devBuilds.addActionListener(this);
		buildsCombo.addActionListener(this);

		clipboardCheckbox.setSelected(Settings.allowClipboardAccess());
		retryLoginCheckbox.setSelected(Settings.getLoginTries() > 1);
		latestLWJGLCheckbox.setSelected(Settings.isLatestLWJGL());
		alwaysUpdateCheckbox.setSelected(Settings.isAcceptUpdates());

		setResizable(false);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		memoryCombo.addItem("512MB");
		memoryCombo.addItem("1GB");
		memoryCombo.addItem("2GB");
		memoryCombo.addItem("4GB");
		memoryCombo.addItem("8GB");
		memoryCombo.addItem("16GB");

		memoryCombo.setSelectedIndex(Settings.getMemory());

		JLabel lblMemoryToAllocate = new JLabel("Memory to allocate: ");

		JLabel selectBuild = new JLabel("Select Spoutcraft build: ");

		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
				gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.add(gl_contentPanel.createSequentialGroup()
								.addContainerGap()
								.add(gl_contentPanel.createParallelGroup(Alignment.LEADING)
										.add(gl_contentPanel.createSequentialGroup()
												.add(selectBuild)
												.addPreferredGap(LayoutStyle.RELATED)
												.add(buildsCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
										.add(devBuilds)
										.add(recBuilds)
										.add(customBuilds)
										.add(alwaysUpdateCheckbox)
										.add(clipboardCheckbox)
										.add(retryLoginCheckbox)
										.add(latestLWJGLCheckbox)
										.add(clearCache)
										.add(buildInfo)
										.add(gl_contentPanel.createSequentialGroup()
												.add(lblMemoryToAllocate)
												.addPreferredGap(LayoutStyle.RELATED)
												.add(memoryCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
								.addContainerGap(27, Short.MAX_VALUE))
		);

		Font font = new Font("Arial", Font.PLAIN, 11);
		clipboardCheckbox.setFont(font);
		devBuilds.setFont(font);
		recBuilds.setFont(font);
		retryLoginCheckbox.setFont(font);
		clearCache.setFont(font);
		clearCache.setActionCommand("Clear Cache");
		clearCache.addActionListener(this);
		gl_contentPanel.setVerticalGroup(
				gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.add(gl_contentPanel.createSequentialGroup()
								.add(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
										.add(selectBuild)
										.add(buildsCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.add(devBuilds)
								.add(recBuilds)
								.add(customBuilds)
								.add(alwaysUpdateCheckbox)
								.add(retryLoginCheckbox)
								.addPreferredGap(LayoutStyle.RELATED)
								.add(clipboardCheckbox)
								.add(latestLWJGLCheckbox)
								.addPreferredGap(LayoutStyle.RELATED)
								.add(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
										.add(memoryCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.add(lblMemoryToAllocate))
								.addPreferredGap(LayoutStyle.RELATED)
								.add(clearCache)
								.addPreferredGap(LayoutStyle.RELATED)
								.add(buildInfo)
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

	public void updateBuildsList() {
		if (buildsCombo.getItemCount() == 0) {
			String[] buildList = MinecraftDownloadUtils.getSpoutcraftBuilds();
			if (buildList != null) {
				for (String item : buildList) {
					buildsCombo.addItem(item);
				}
			} else {
				buildsCombo.addItem("No builds found");
			}
			updateBuildsCombo();
		}
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
	}

	public void actionPerformed(ActionEvent evt) {
		String id = evt.getActionCommand();
		if (id.equals("OK")) {
			if (recBuilds.isSelected()) {
				Settings.setSpoutcraftBuild("RECOMMENDED");
			} else if (devBuilds.isSelected()) {
				Settings.setSpoutcraftBuild("DEV");
			} else if (buildsCombo.isEnabled()) {
				int build = getSelectedBuild();
				Settings.setSpoutcraftBuild("CUSTOM");
				if (build > -1) {
					Settings.setSpoutcraftSelectedBuild(build);
				}
			}
			Settings.setClipboardAccess(clipboardCheckbox.isSelected());
			Settings.setRetryLogin(retryLoginCheckbox.isSelected());
			Settings.setAcceptUpdates(alwaysUpdateCheckbox.isSelected());
			if (Settings.getMemory() > 5) {
				Settings.setMemory(0);
			}
			if (memoryCombo.getSelectedIndex() != Settings.getMemory()) {
				Settings.setMemory(memoryCombo.getSelectedIndex());
				int mem = 1 << 9 + memoryCombo.getSelectedIndex();
				// TODO Main.reboot("-Xmx" + mem + "m");
			}
			if (latestLWJGLCheckbox.isSelected() != Settings.isLatestLWJGL()) {
				Settings.setLatestLWJGL(latestLWJGLCheckbox.isSelected());
				clearCache();
			}


			this.setVisible(false);
			this.dispose();
		} else if (id.equals("Cancel")) {
			this.setVisible(false);
			this.dispose();
		} else if (id.equals("Clear Cache")) {
			this.setAlwaysOnTop(false);
			if (clearCache()) {
				JOptionPane.showMessageDialog(getParent(), "Successfully cleared the cache.");
			} else {
				JOptionPane.showMessageDialog(getParent(), "Failed to clear the cache! Ensure spoutcraft files are open.\nIf all else fails, close the launcher, restart it, and try again.");
			}
			this.setAlwaysOnTop(true);
		} else if (id.equals(customBuilds.getText()) || id.equals(devBuilds.getText()) || id.equals(recBuilds.getText())) {
			updateBuildsCombo();
		}
	}

	public int getSelectedBuild() {
		int build = -1;
		try {
			String item = ((String) buildsCombo.getSelectedItem());
			if (item.contains("|")) {
				item = item.split("\\|")[0];
			}
			item.trim();
			build = Integer.parseInt(item);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return build;
	}

	public void updateBuildsCombo() {
		buildsCombo.setEnabled(customBuilds.isSelected());

		if (customBuilds.isSelected()) {
			if (Settings.getSpoutcraftSelectedBuild() > -1) {
				int build = Settings.getSpoutcraftSelectedBuild();
				for (int i = 0; i < buildsCombo.getItemCount(); i++) {
					String item = (String) buildsCombo.getItemAt(i);
					if (item.contains(String.valueOf(build))) {
						buildsCombo.setSelectedIndex(i);
						break;
					}
				}
			}
		} else if (devBuilds.isSelected()) {
			buildsCombo.setSelectedIndex(0);
		} else if (recBuilds.isSelected()) {
			for (int i = 0; i < buildsCombo.getItemCount(); i++) {
				String item = (String) buildsCombo.getItemAt(i);
				if (item.contains("Rec. Build")) {
					buildsCombo.setSelectedIndex(i);
					break;
				}
			}
		}
	}

	public static boolean clearCache() {
		return Launcher.clearCache();
	}
}
