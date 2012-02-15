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
import org.jdesktop.swingworker.SwingWorker;
import org.spoutcraft.launcher.api.Build;
import org.spoutcraft.launcher.api.Launcher;
import org.spoutcraft.launcher.api.OptionsFrame;
import org.spoutcraft.launcher.api.skin.gui.Alignment;
import org.spoutcraft.launcher.api.util.MirrorUtils;
import org.spoutcraft.launcher.util.MinecraftDownloadUtils;
import org.spoutcraft.launcher.yml.LauncherYML;
import org.spoutcraft.launcher.yml.LibrariesYML;
import org.spoutcraft.launcher.yml.MinecraftYML;
import org.spoutcraft.launcher.yml.SpecialYML;
import org.spoutcraft.launcher.yml.SpoutcraftYML;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class SimpleOptionsFrame extends OptionsFrame implements ActionListener {

	private JPanel contentPane = new JPanel();

	private JLabel spoutcraftLbl = new JLabel("Spoutcraft Update Settings:");
	private JRadioButton scRecBuild = new JRadioButton("Always use recommended builds");
	private JRadioButton scDevBuild = new JRadioButton("Always use development builds");
	private JRadioButton scCustomBuild = new JRadioButton("Manual build selection");
	private JComboBox scBuildsList = new JComboBox();

	private JLabel launcherLbl = new JLabel("Launcher Update Setttings");
	private JRadioButton launcherRecBuild = new JRadioButton("Always use recommended builds");
	private JRadioButton launcherDevBuild = new JRadioButton("Always use development builds");
	private JRadioButton launcherCustomBuild = new JRadioButton("Manual build selection");
	private JComboBox launcherBuildsList = new JComboBox();

	private JCheckBox alwaysUptdate = new JCheckBox("Always install updates");
	private JCheckBox retryLogin = new JCheckBox("Retry after connection timeout");
	private JCheckBox useLatestLWGJL = new JCheckBox("Use the latest LWJGL");
	private JCheckBox clibboardAccess = new JCheckBox("Allow clipboard access");

	private JButton save = new JButton("Save");
	private JButton cancel = new JButton("Cancel");

	private SwingWorker worker = null;

	public SimpleOptionsFrame() {

		// Set up radio buttons \\
		ButtonGroup scUpdateSettings = new ButtonGroup();
		scUpdateSettings.add(scRecBuild);
		scUpdateSettings.add(scDevBuild);
		scUpdateSettings.add(scCustomBuild);


		ButtonGroup launcherUpdateSettings = new ButtonGroup();
		launcherUpdateSettings.add(launcherRecBuild);
		launcherUpdateSettings.add(launcherDevBuild);
		launcherUpdateSettings.add(launcherCustomBuild);

		scBuildsList.addItem("Loading...");
		launcherBuildsList.addItem("Loading...");

		switch (Settings.getSpoutcraftBuild()) {
			case RECOMMENDED:
				scRecBuild.setSelected(true);
				scDevBuild.setSelected(false);
				scCustomBuild.setSelected(false);
				break;
			case DEV:
				scRecBuild.setSelected(false);
				scDevBuild.setSelected(true);
				scCustomBuild.setSelected(false);
				break;
			case CUSTOM:
				scRecBuild.setSelected(false);
				scDevBuild.setSelected(false);
				scCustomBuild.setSelected(true);
				break;
		}

		switch (Settings.getLauncherBuild()) {
			case RECOMMENDED:
				launcherRecBuild.setSelected(true);
				scDevBuild.setSelected(false);
				launcherCustomBuild.setSelected(false);
				break;
			case DEV:
				launcherRecBuild.setSelected(false);
				launcherDevBuild.setSelected(true);
				launcherCustomBuild.setSelected(false);
				break;
			case CUSTOM:
				launcherRecBuild.setSelected(false);
				launcherDevBuild.setSelected(false);
				launcherCustomBuild.setSelected(true);
				break;
		}

		alwaysUptdate.setSelected(Settings.isAcceptUpdates());
		clibboardAccess.setSelected(Settings.allowClipboardAccess());
		useLatestLWGJL.setSelected(Settings.isLatestLWJGL());
		retryLogin.setSelected(Settings.retryLogin());


		// Layout \\
		GroupLayout layout = new GroupLayout(contentPane);
		layout.setHorizontalGroup(
				layout.createParallelGroup(Alignment.LEADING)
						.add(layout.createSequentialGroup()
								.addContainerGap()
								.add(layout.createParallelGroup(Alignment.LEADING)
										.add(spoutcraftLbl)
										.add(scRecBuild)
										.add(scDevBuild)
										.add(layout.createSequentialGroup()
												.add(scCustomBuild)
												.addPreferredGap(LayoutStyle.RELATED)
												.add(scBuildsList, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)

										)
										.add(launcherLbl)
										.add(launcherRecBuild)
										.add(launcherDevBuild)
										.add(layout.createSequentialGroup()
												.add(launcherCustomBuild)
												.addPreferredGap(LayoutStyle.RELATED)
												.add(launcherBuildsList, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)

										)
										.add(clibboardAccess)
										.add(retryLogin)
										.add(useLatestLWGJL)
										.add(alwaysUptdate)
										.add(layout.createSequentialGroup()
												.addContainerGap(50, 50)
												.add(save)
												.addContainerGap(10, 10)
												.add(cancel)

										)

								)

						)
		);

		layout.setVerticalGroup(
				layout.createParallelGroup(Alignment.LEADING)
						.add(layout.createSequentialGroup()
								.add(spoutcraftLbl)
								.add(scRecBuild)
								.add(scDevBuild)
								.add(layout.createParallelGroup(Alignment.BASELINE)
										.add(scCustomBuild)
										.add(scBuildsList, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								)
								.addContainerGap(10, 10)
								.add(launcherLbl)
								.add(launcherRecBuild)
								.add(launcherDevBuild)
								.add(layout.createParallelGroup(Alignment.BASELINE)
										.add(launcherCustomBuild)
										.add(launcherBuildsList, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)

								)
								.addContainerGap(10, 10)
								.add(clibboardAccess)
								.add(retryLogin)
								.add(useLatestLWGJL)
								.add(alwaysUptdate)
								.addContainerGap(10, 10)
								.add(layout.createParallelGroup(Alignment.BASELINE)
										.add(save)
										.add(cancel)
								)

						)
		);
		contentPane.setLayout(layout);

		getContentPane().setLayout(new BorderLayout());
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPane, BorderLayout.CENTER);

		worker = new SwingWorker<Object, Object>() {
			protected Object doInBackground() throws Exception {
				MirrorUtils.updateMirrorsYMLCache();
				SpoutcraftYML.updateSpoutcraftYMLCache();
				//LauncherYML.updateLauncherYMLCache();

				buildSpoutcraftBuildList();
				return null;
			}
		};

		worker.execute();

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		pack();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(save.getActionCommand())) {
			// Save the settings \\
			if (scRecBuild.isSelected()) {
				Settings.setSpoutcraftBuild(Build.RECOMMENDED);
			} else if (scDevBuild.isSelected()) {
				Settings.setSpoutcraftBuild(Build.DEV);
			} else if (scCustomBuild.isSelected()) {
				Settings.setSpoutcraftBuild(Build.CUSTOM);
				Settings.setSpoutcraftSelectedBuild(234);
			}
		} else if (e.getActionCommand().equals(scRecBuild.getActionCommand()) || e.getActionCommand().equals(scDevBuild.getActionCommand()) || e.getActionCommand().equals(scCustomBuild.getActionCommand())) {
			refreshSpoutcraftBuildList();
		} else if (e.getActionCommand().equals(launcherRecBuild.getActionCommand()) || e.getActionCommand().equals(launcherDevBuild.getActionCommand()) || e.getActionCommand().equals(launcherCustomBuild.getActionCommand())) {
			refreshLauncherBuildList();
		}
	}

	public void buildSpoutcraftBuildList() {
		scBuildsList.removeAllItems();
		String[] buildList = MinecraftDownloadUtils.getSpoutcraftBuilds();
		if (buildList != null) {
			for (String item : buildList) {
				scBuildsList.addItem(item);
			}
		} else {
			scBuildsList.addItem("No builds found");
		}
		pack();
		setBounds(getX(), getY(), getWidth() + 10, getHeight());
		refreshSpoutcraftBuildList();
	}

	public void refreshSpoutcraftBuildList() {
		scBuildsList.setEnabled(scCustomBuild.isSelected());

		if (scCustomBuild.isSelected()) {
			if (Settings.getSpoutcraftSelectedBuild() > -1) {
				int build = Settings.getSpoutcraftSelectedBuild();
				for (int i = 0; i < scBuildsList.getItemCount(); i++) {
					String item = (String) scBuildsList.getItemAt(i);
					if (item.contains(String.valueOf(build))) {
						scBuildsList.setSelectedIndex(i);
						break;
					}
				}
			}
		} else if (scDevBuild.isSelected()) {
			scBuildsList.setSelectedIndex(0);
		} else if (scRecBuild.isSelected()) {
			for (int i = 0; i < scBuildsList.getItemCount(); i++) {
				String item = (String) scBuildsList.getItemAt(i);
				if (item.contains("Rec. Build")) {
					scBuildsList.setSelectedIndex(i);
					break;
				}
			}
		}
	}

	public void refreshLauncherBuildList() {

	}

	public void exit() {
		worker.cancel(true);
		super.exit();
	}
}
