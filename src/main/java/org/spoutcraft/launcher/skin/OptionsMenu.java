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
package org.spoutcraft.launcher.skin;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.URL;
import java.util.List;

import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;

import org.spoutcraft.launcher.Channel;
import org.spoutcraft.launcher.Memory;
import org.spoutcraft.launcher.Proxy;
import org.spoutcraft.launcher.Settings;
import org.spoutcraft.launcher.WindowMode;
import org.spoutcraft.launcher.api.Launcher;
import org.spoutcraft.launcher.entrypoint.SpoutcraftLauncher;
import org.spoutcraft.launcher.exceptions.RestfulAPIException;
import org.spoutcraft.launcher.rest.SpoutcraftBuild;
import org.spoutcraft.launcher.rest.Versions;
import org.spoutcraft.launcher.util.Compatibility;
import org.spoutcraft.launcher.util.Utils;

@SuppressWarnings({"restriction"})
public class OptionsMenu extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;
	private static final URL spoutcraftIcon = SpoutcraftLauncher.class.getResource("/org/spoutcraft/launcher/resources/icon.png");
	private static final String LOGS_ACTION = "logs";
	private static final String CANCEL_ACTION = "cancel";
	private static final String RESET_ACTION = "reset";
	private static final String SAVE_ACTION = "save";
	private static final String SPOUTCRAFT_CHANNEL_ACTION = "spoutcraft_channel";
	private JTabbedPane mainOptions;
	private JPanel gamePane;
	private JLabel spoutcraftVersionLabel;
	private JLabel memoryLabel;
	private JComboBox spoutcraftVersion;
	private JComboBox memory;
	private JLabel minecraftVersionLabel;
	private JComboBox minecraftVersion;
	private JPanel proxyPane;
	private JLabel proxyHostLabel;
	private JLabel proxyPortLabel;
	private JLabel proxyUsernameLabel;
	private JLabel passwordLabel;
	private JTextField proxyHost;
	private JTextField proxyPort;
	private JTextField proxyUser;
	private JPasswordField proxyPass;
	private JPanel developerPane;
	private JLabel DevLabel;
	private JTextField developerCode;
	private JLabel launcherVersionLabel;
	private JComboBox launcherVersion;
	private JLabel debugLabel;
	private JCheckBox debugMode;
	private JLabel lwjglLabel;
	private JCheckBox latestLWJGL;
	private JLabel md5Label;
	private JCheckBox md5Checkbox;
	private JLabel buildLabel;
	private JComboBox buildCombo;
	private JLabel serverLabel;
	private JTextField directJoin;
	private JButton logsButton;
	private JButton resetButton;
	private JButton cancelButton;
	private JButton saveButton;
	private JLabel windowModeLabel;
	private JComboBox windowMode;

	public OptionsMenu() {
		initComponents();

		setTitle("Launcher Options");
		Compatibility.setIconImage(this, Toolkit.getDefaultToolkit().getImage(spoutcraftIcon));
		setResizable(false);

		populateMemory(memory);

		logsButton.addActionListener(this);
		logsButton.setActionCommand(LOGS_ACTION);

		cancelButton.addActionListener(this);
		cancelButton.setActionCommand(CANCEL_ACTION);

		resetButton.addActionListener(this);
		resetButton.setActionCommand(RESET_ACTION);

		saveButton.addActionListener(this);
		saveButton.setActionCommand(SAVE_ACTION);

		developerCode.setText(Settings.getDeveloperCode());
		developerCode.getDocument().addDocumentListener(new DeveloperCodeListener(developerCode));

		Settings.setSpoutcraftChannel(populateChannelVersion(spoutcraftVersion, Settings.getSpoutcraftChannel().type(), true));
		Settings.setLauncherChannel(populateChannelVersion(launcherVersion, Settings.getLauncherChannel().type(), false));
		populateMinecraftVersions(minecraftVersion);
		populateSpoutcraftBuilds(buildCombo);
		Settings.setWindowModeId(populateWindowMode(windowMode));

		spoutcraftVersion.addActionListener(this);
		spoutcraftVersion.setActionCommand(SPOUTCRAFT_CHANNEL_ACTION);
		updateBuildList();
		this.md5Checkbox.setSelected(Settings.isIgnoreMD5());
		this.debugMode.setSelected(Settings.isDebugMode());
		directJoin.setText(Settings.getDirectJoin());
	}

	private boolean isValidDeveloperCode() {
		return true;
	}

	private void populateSpoutcraftBuilds(JComboBox builds) {
		if (!isValidDeveloperCode()) {
			return;
		}
		try {
			List<SpoutcraftBuild> buildList = SpoutcraftBuild.getBuildList();
			for (SpoutcraftBuild build : buildList) {
				builds.addItem(build);
			}
			String selected = Settings.getSpoutcraftSelectedBuild();
			for (int i = 0; i < buildList.size(); i++) {
				if (buildList.get(i).getBuildNumber().equals(selected)) {
					builds.setSelectedIndex(i);
					break;
				}
			}
		} catch (RestfulAPIException e) {
			builds.setEnabled(false);
			builds.setToolTipText("Error retrieving build list");
			e.printStackTrace();
		}
	}

	private String getSelectedSpoutcraftBuild() {
		if (Channel.getType(spoutcraftVersion.getSelectedIndex()) == Channel.CUSTOM) {
			return ((SpoutcraftBuild) buildCombo.getSelectedItem()).getBuildNumber();
		}
		return "-1";
	}

	private String getSelectedMinecraftVersion() {
		if (Channel.getType(spoutcraftVersion.getSelectedIndex()) == Channel.CUSTOM) {
			return ((SpoutcraftBuild) buildCombo.getSelectedItem()).getMinecraftVersion();
		}
		return minecraftVersion.getSelectedItem().toString();
	}

	private void populateMinecraftVersions(JComboBox minecraftVersion) {
		final String selected = Settings.getMinecraftVersion();
		minecraftVersion.addItem("Latest");
		for (String version : Versions.getMinecraftVersions()) {
			minecraftVersion.addItem(version);
		}
		boolean found = false;
		for (int i = 0; i < minecraftVersion.getItemCount(); i++) {
			String item = minecraftVersion.getItemAt(i).toString();
			if (item.equalsIgnoreCase(selected)) {
				minecraftVersion.setSelectedIndex(i);
				found = true;
				break;
			}
		}
		if (!found) {
			Settings.setMinecraftVersion(Settings.DEFAULT_MINECRAFT_VERSION);
			minecraftVersion.setSelectedIndex(0);
		}
	}

	private int populateWindowMode(JComboBox window) {
		int id = Settings.getWindowModeId();
		for (WindowMode m : WindowMode.values()) {
			window.addItem(m.getModeName());
		}
		if (id >= 0 && id < WindowMode.values().length) {
			window.setSelectedIndex(id);
			return id;
		}
		return WindowMode.WINDOWED.getId();
	}

	private Channel populateChannelVersion(JComboBox version, int selection, boolean custom) {
		version.addItem("Stable");
		version.addItem("Beta");
		if (isValidDeveloperCode()) {
			version.addItem("Dev");
			if (custom) {
				version.addItem("Custom");
			}
		} else if (selection > 1 || selection < 0) {
			selection = 0;
		}
		version.setSelectedIndex(selection);
		return Channel.getType(selection);
	}

	private void populateMemory(JComboBox memory) {
		long maxMemory = 1024;
		String architecture = System.getProperty("sun.arch.data.model", "32");
		boolean bit64 = architecture.equals("64");

		try {
			OperatingSystemMXBean osInfo = ManagementFactory.getOperatingSystemMXBean();
			if (osInfo instanceof com.sun.management.OperatingSystemMXBean) {
				maxMemory = ((com.sun.management.OperatingSystemMXBean) osInfo).getTotalPhysicalMemorySize() / 1024 / 1024;
			}
		} catch (Throwable t) {
		}
		maxMemory = Math.max(512, maxMemory);

		if (maxMemory >= Memory.MAX_32_BIT_MEMORY && !bit64) {
			memory.setToolTipText("<html>Sets the amount of memory assigned to Spoutcraft<br/>" +
					"You have more than 1.5GB of memory available, but<br/>" +
					"you must have 64bit java installed to use it.</html>");
		} else {
			memory.setToolTipText("<html>Sets the amount of memory assigned to Spoutcraft<br/>" +
					"More memory is not always better.<br/>" +
					"More memory will also cause your CPU to work more.</html>");
		}

		if (!bit64) {
			maxMemory = Math.min(Memory.MAX_32_BIT_MEMORY, maxMemory);
		}
		System.out.println("Maximum usable memory detected: " + maxMemory + " mb");

		for (Memory mem : Memory.memoryOptions) {
			if (maxMemory >= mem.getMemoryMB()) {
				memory.addItem(mem.getDescription());
			}
		}

		int memoryOption = Settings.getMemory();
		try {
			Settings.setMemory(memoryOption);
			memory.setSelectedIndex(Memory.getMemoryIndexFromId(memoryOption));
		} catch (IllegalArgumentException e) {
			memory.removeAllItems();
			memory.addItem(String.valueOf(Memory.memoryOptions[0]));
			Settings.setMemory(1); // 512 == 1
			memory.setSelectedIndex(0); // 1st element
		}
	}

	public void actionPerformed(ActionEvent e) {
		action(e.getActionCommand());
	}

	private void action(String command) {
		if (command.equals(CANCEL_ACTION)) {
			closeForm();
		} else if (command.equals(RESET_ACTION)) {

		} else if (command.equals(LOGS_ACTION)) {
			try {
				File logDirectory = new File(Utils.getWorkingDirectory(), "logs");
				Compatibility.open(logDirectory);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (command.equals(SAVE_ACTION)) {
			Channel prev = Settings.getSpoutcraftChannel();
			String build = Settings.getSpoutcraftSelectedBuild();
			String minecraftVersion = Settings.getMinecraftVersion();
			boolean oldDebug = Settings.isDebugMode();

			// Save
			Settings.setLauncherChannel(Channel.getType(launcherVersion.getSelectedIndex()));
			Settings.setSpoutcraftChannel(Channel.getType(spoutcraftVersion.getSelectedIndex()));
			Settings.setMemory(Memory.memoryOptions[memory.getSelectedIndex()].getSettingsId());
			Settings.setDebugMode(debugMode.isSelected());
			Settings.setIgnoreMD5(md5Checkbox.isSelected());
			Settings.setWindowModeId(windowMode.getSelectedIndex());
			Settings.setMinecraftVersion(getSelectedMinecraftVersion());
			Settings.setSpoutcraftSelectedBuild(getSelectedSpoutcraftBuild());
			Settings.setProxyHost(this.proxyHost.getText());
			Settings.setProxyPort(this.proxyPort.getText());
			Settings.setProxyUsername(this.proxyUser.getText());
			Settings.setProxyPassword(this.proxyPass.getPassword());
			Settings.setDirectJoin(this.directJoin.getText());
			Proxy proxy = new Proxy();
			proxy.setHost(Settings.getProxyHost());
			proxy.setPort(Settings.getProxyPort());
			proxy.setUser(Settings.getProxyUsername());
			proxy.setPass(Settings.getProxyPassword().toCharArray());
			proxy.setup();
			Settings.getYAML().save();
			closeForm();

			// Inform the updating thread
			if (prev != Settings.getSpoutcraftChannel() || !build.equals(Settings.getSpoutcraftSelectedBuild()) || !minecraftVersion.equals(Settings.getMinecraftVersion())) {
				Launcher.getGameUpdater().onSpoutcraftBuildChange();
			}

			if (Settings.isDebugMode() || oldDebug) {
				SpoutcraftLauncher.setupConsole();
			}
		} else if (command.equals(SPOUTCRAFT_CHANNEL_ACTION)) {
			updateBuildList();
		}
	}

	private void updateBuildList() {
		if (Channel.CUSTOM == Channel.getType(spoutcraftVersion.getSelectedIndex())) {
			buildCombo.setEnabled(true);
		} else {
			buildCombo.setEnabled(false);
		}
	}

	private void closeForm() {
		this.dispose();
		this.setVisible(false);
		this.setAlwaysOnTop(false);
	}

	private void initComponents() {
		mainOptions = new JTabbedPane();
		gamePane = new JPanel();
		spoutcraftVersionLabel = new JLabel();
		memoryLabel = new JLabel();
		spoutcraftVersion = new JComboBox();
		memory = new JComboBox();
		minecraftVersionLabel = new JLabel();
		minecraftVersion = new JComboBox();
		windowModeLabel = new JLabel();
		windowMode = new JComboBox();
		proxyPane = new JPanel();
		proxyHostLabel = new JLabel();
		proxyPortLabel = new JLabel();
		proxyUsernameLabel = new JLabel();
		passwordLabel = new JLabel();
		proxyHost = new JTextField();
		proxyPort = new JTextField();
		proxyUser = new JTextField();
		proxyPass = new JPasswordField();
		developerPane = new JPanel();
		DevLabel = new JLabel();
		developerCode = new JTextField();
		launcherVersionLabel = new JLabel();
		launcherVersion = new JComboBox();
		debugLabel = new JLabel();
		debugMode = new JCheckBox();
		lwjglLabel = new JLabel();
		latestLWJGL = new JCheckBox();
		md5Label = new JLabel();
		md5Checkbox = new JCheckBox();
		buildLabel = new JLabel();
		buildCombo = new JComboBox();
		serverLabel = new JLabel();
		directJoin = new JTextField();
		logsButton = new JButton();
		resetButton = new JButton();
		cancelButton = new JButton();
		saveButton = new JButton();

		//======== this ========
		Container contentPane = getContentPane();

		//======== mainOptions ========
		{
			mainOptions.setFont(new Font("Arial", Font.PLAIN, 11));

			//======== gamePane ========
			{
				gamePane.setFont(new Font("Arial", Font.PLAIN, 11));

				//---- spoutcraftVersionLabel ----
				spoutcraftVersionLabel.setText("Version:");
				spoutcraftVersionLabel.setFont(new Font("Arial", Font.PLAIN, 11));

				//---- memoryLabel ----
				memoryLabel.setText("Memory:");
				memoryLabel.setBackground(Color.white);
				memoryLabel.setFont(new Font("Arial", Font.PLAIN, 11));

				//---- spoutcraftVersion ----
				spoutcraftVersion.setFont(new Font("Arial", Font.PLAIN, 11));

				//---- memory ----
				memory.setFont(new Font("Arial", Font.PLAIN, 11));

				//---- minecraftVersionLabel ----
				minecraftVersionLabel.setText("Minecraft:");
				minecraftVersionLabel.setFont(new Font("Arial", Font.PLAIN, 11));

				//---- minecraftVersion ----
				minecraftVersion.setFont(new Font("Arial", Font.PLAIN, 11));
				minecraftVersion.setToolTipText("The minecraft version");

				//---- windowModeLabel ----
				windowModeLabel.setText("Window Mode:");
				windowModeLabel.setFont(new Font("Arial", Font.PLAIN, 11));

				//---- windowMode ----
				windowMode.setFont(new Font("Arial", Font.PLAIN, 11));
				windowMode.setToolTipText("<html>Windowed - Starts the game in a smaller 900x540 window<br/>" +
						"Full Screen - Starts the game using the full monitor resolution, ontop.<br/>" +
						"Maximized - Starts the game with the maximimum size</html>");

				GroupLayout gamePaneLayout = new GroupLayout(gamePane);
				gamePane.setLayout(gamePaneLayout);
				gamePaneLayout.setHorizontalGroup(
						gamePaneLayout.createParallelGroup()
								.add(gamePaneLayout.createSequentialGroup()
										.addContainerGap()
										.add(gamePaneLayout.createParallelGroup()
												.add(gamePaneLayout.createSequentialGroup()
														.add(memoryLabel)
														.addPreferredGap(LayoutStyle.UNRELATED)
														.add(memory))
												.add(gamePaneLayout.createSequentialGroup()
														.add(gamePaneLayout.createParallelGroup()
																.add(minecraftVersionLabel)
																.add(spoutcraftVersionLabel))
														.addPreferredGap(LayoutStyle.RELATED)
														.add(gamePaneLayout.createParallelGroup()
																.add(spoutcraftVersion, GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
																.add(minecraftVersion)))
												.add(gamePaneLayout.createSequentialGroup()
														.add(windowModeLabel)
														.addPreferredGap(LayoutStyle.RELATED)
														.add(windowMode, GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)))

												//.add(gamePaneLayout.createSequentialGroup()
												//.add(logsButton)
												//.addPreferredGap(LayoutStyle.RELATED)
										.addContainerGap())
				);
				gamePaneLayout.setVerticalGroup(
						gamePaneLayout.createParallelGroup()
								.add(gamePaneLayout.createSequentialGroup()
										.addContainerGap()
										.add(gamePaneLayout.createParallelGroup(GroupLayout.BASELINE)
												.add(minecraftVersionLabel)
												.add(minecraftVersion, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(LayoutStyle.RELATED)
										.add(gamePaneLayout.createParallelGroup(GroupLayout.BASELINE)
												.add(spoutcraftVersionLabel)
												.add(spoutcraftVersion, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(LayoutStyle.RELATED)
										.add(gamePaneLayout.createParallelGroup(GroupLayout.BASELINE)
												.add(memoryLabel)
												.add(memory, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(LayoutStyle.RELATED)
										.add(gamePaneLayout.createParallelGroup(GroupLayout.BASELINE)
												.add(windowModeLabel, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
												.add(windowMode, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
										.addContainerGap(86, Short.MAX_VALUE))
				);
			}
			mainOptions.addTab("Game", gamePane);

			//======== proxyPane ========
			{
				//---- proxyHostLabel ----
				proxyHostLabel.setText("Proxy Host:");
				proxyHostLabel.setFont(new Font("Arial", Font.PLAIN, 11));

				//---- proxyPortLabel ----
				proxyPortLabel.setText("Proxy Port:");
				proxyPortLabel.setFont(new Font("Arial", Font.PLAIN, 11));

				//---- proxyUsername ----
				proxyUsernameLabel.setText("Username:");
				proxyUsernameLabel.setFont(new Font("Arial", Font.PLAIN, 11));

				//---- passwordLabel ----
				passwordLabel.setText("Password:");
				passwordLabel.setFont(new Font("Arial", Font.PLAIN, 11));

				//---- proxyHost ----
				proxyHost.setFont(new Font("Arial", Font.PLAIN, 11));
				proxyHost.setToolTipText("The host or IP address of the proxy");

				//---- proxyPort ----
				proxyPort.setFont(new Font("Arial", Font.PLAIN, 11));
				proxyPort.setToolTipText("The port (if any) for the proxy");

				//---- proxyUser ----
				proxyUser.setFont(new Font("Arial", Font.PLAIN, 11));
				proxyUser.setToolTipText("The username, if required, for the proxy");

				//---- proxyPass ----
				proxyPass.setFont(new Font("Arial", Font.PLAIN, 11));
				proxyPass.setToolTipText("The password, if required, for the proxy");

				GroupLayout proxyPaneLayout = new GroupLayout(proxyPane);
				proxyPane.setLayout(proxyPaneLayout);
				proxyPaneLayout.setHorizontalGroup(
						proxyPaneLayout.createParallelGroup()
								.add(proxyPaneLayout.createSequentialGroup()
										.addContainerGap()
										.add(proxyPaneLayout.createParallelGroup()
												.add(proxyPortLabel)
												.add(proxyHostLabel)
												.add(proxyUsernameLabel)
												.add(passwordLabel))
										.addPreferredGap(LayoutStyle.UNRELATED)
										.add(proxyPaneLayout.createParallelGroup()
												.add(proxyPass, GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
												.add(proxyUser, GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
												.add(proxyHost, GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
												.add(proxyPort, GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE))
										.addContainerGap())
				);
				proxyPaneLayout.setVerticalGroup(
						proxyPaneLayout.createParallelGroup()
								.add(proxyPaneLayout.createSequentialGroup()
										.addContainerGap()
										.add(proxyPaneLayout.createParallelGroup(GroupLayout.BASELINE)
												.add(proxyHostLabel, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
												.add(proxyHost, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(LayoutStyle.RELATED)
										.add(proxyPaneLayout.createParallelGroup(GroupLayout.BASELINE)
												.add(proxyPortLabel)
												.add(proxyPort, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(LayoutStyle.UNRELATED)
										.add(proxyPaneLayout.createParallelGroup(GroupLayout.BASELINE)
												.add(proxyUsernameLabel)
												.add(proxyUser, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(LayoutStyle.RELATED)
										.add(proxyPaneLayout.createParallelGroup(GroupLayout.BASELINE)
												.add(passwordLabel)
												.add(proxyPass, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
										.addContainerGap(82, Short.MAX_VALUE))
				);
			}
			mainOptions.addTab("Proxy", proxyPane);

			//======== developerPane ========
			{
				//---- DevLabel ----
				DevLabel.setText("Developer Code:");
				DevLabel.setFont(new Font("Arial", Font.PLAIN, 11));

				//---- developerCode ----
				developerCode.setFont(new Font("Arial", Font.PLAIN, 11));
				developerCode.setToolTipText("Allows access to advanced settings");

				//---- launcherVersionLabel ----
				launcherVersionLabel.setText("Launcher:");
				launcherVersionLabel.setBackground(Color.white);
				launcherVersionLabel.setFont(new Font("Arial", Font.PLAIN, 11));

				//---- launcherVersion ----
				launcherVersion.setFont(new Font("Arial", Font.PLAIN, 11));

				//---- debugLabel ----
				debugLabel.setText("Debug Mode:");
				debugLabel.setBackground(Color.white);
				debugLabel.setFont(new Font("Arial", Font.PLAIN, 11));

				//---- debugMode ----
				debugMode.setBackground(Color.white);
				debugMode.setFont(new Font("Arial", Font.PLAIN, 11));
				debugMode.setToolTipText("Enables more detailed logging");

				//---- lwjglLabel ----
				lwjglLabel.setText("Latest LWJGL:");
				lwjglLabel.setFont(new Font("Arial", Font.PLAIN, 11));

				//---- latestLWJGL ----
				latestLWJGL.setFont(new Font("Arial", Font.PLAIN, 11));

				//---- md5Label ----
				md5Label.setText("Disable MD5:");
				md5Label.setFont(new Font("Arial", Font.PLAIN, 11));

				//---- md5Checkbox ----
				md5Checkbox.setFont(new Font("Arial", Font.PLAIN, 11));
				md5Checkbox.setToolTipText("Disables MD5 hashsum checks on the files");

				//---- buildLabel ----
				buildLabel.setText("Build:");
				buildLabel.setFont(new Font("Arial", Font.PLAIN, 11));

				//---- buildCombo ----
				buildCombo.setFont(new Font("Arial", Font.PLAIN, 11));

				//---- serverLabel ----
				serverLabel.setText("Direct Join:");
				serverLabel.setFont(new Font("Arial", Font.PLAIN, 11));

				//---- directJoin ----
				directJoin.setFont(new Font("Arial", Font.PLAIN, 11));

				GroupLayout developerPaneLayout = new GroupLayout(developerPane);
				developerPane.setLayout(developerPaneLayout);
				developerPaneLayout.setHorizontalGroup(
						developerPaneLayout.createParallelGroup()
								.add(developerPaneLayout.createSequentialGroup()
										.addContainerGap()
										.add(developerPaneLayout.createParallelGroup()
												.add(developerPaneLayout.createSequentialGroup()
														.add(DevLabel)
														.addPreferredGap(LayoutStyle.RELATED)
														.add(developerCode))
												.add(developerPaneLayout.createSequentialGroup()
														.add(launcherVersionLabel)
														.addPreferredGap(LayoutStyle.RELATED)
														.add(launcherVersion))
												.add(developerPaneLayout.createSequentialGroup()
														.add(serverLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
														.addPreferredGap(LayoutStyle.RELATED)
														.add(directJoin, GroupLayout.PREFERRED_SIZE, 196, GroupLayout.PREFERRED_SIZE))
												.add(developerPaneLayout.createSequentialGroup()
														.add(buildLabel)
														.addPreferredGap(LayoutStyle.RELATED)
														.add(buildCombo))
												.add(developerPaneLayout.createSequentialGroup()
														.add(developerPaneLayout.createParallelGroup()
																.add(developerPaneLayout.createSequentialGroup()
																		.add(developerPaneLayout.createParallelGroup()
																				.add(debugLabel)
																				.add(lwjglLabel, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE))
																		.addPreferredGap(LayoutStyle.RELATED)
																		.add(developerPaneLayout.createParallelGroup()
																				.add(latestLWJGL)
																				.add(debugMode)))
																.add(developerPaneLayout.createSequentialGroup()
																		.add(md5Label, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(LayoutStyle.RELATED)
																		.add(md5Checkbox)))
														.add(0, 0, Short.MAX_VALUE)))
										.addContainerGap())
				);
				developerPaneLayout.setVerticalGroup(
						developerPaneLayout.createParallelGroup()
								.add(developerPaneLayout.createSequentialGroup()
										.addContainerGap()
										.add(developerPaneLayout.createParallelGroup(GroupLayout.BASELINE)
												.add(DevLabel)
												.add(developerCode, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(LayoutStyle.RELATED)
										.add(developerPaneLayout.createParallelGroup(GroupLayout.BASELINE)
												.add(launcherVersionLabel)
												.add(launcherVersion, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(LayoutStyle.RELATED)
										.add(developerPaneLayout.createParallelGroup(GroupLayout.BASELINE)
												.add(buildCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
												.add(buildLabel))
										.addPreferredGap(LayoutStyle.RELATED)
										.add(developerPaneLayout.createParallelGroup()
												.add(debugMode)
												.add(debugLabel, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(LayoutStyle.RELATED)
										.add(developerPaneLayout.createParallelGroup(GroupLayout.TRAILING)
												.add(lwjglLabel, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
												.add(latestLWJGL))
										.addPreferredGap(LayoutStyle.RELATED)
										.add(developerPaneLayout.createParallelGroup(GroupLayout.TRAILING)
												.add(md5Label, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
												.add(md5Checkbox))
										.addPreferredGap(LayoutStyle.RELATED)
										.add(developerPaneLayout.createParallelGroup(GroupLayout.BASELINE)
												.add(serverLabel, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
												.add(directJoin, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
										.addContainerGap(5, Short.MAX_VALUE))
				);
			}
			mainOptions.addTab("Developer", developerPane);
		}

		//---- logsButton ----
		logsButton.setText("Logs");

		//---- resetButton ----
		resetButton.setText("Reset");

		//---- cancelButton ----
		cancelButton.setText("Cancel");

		//---- saveButton ----
		saveButton.setText("OK");

		GroupLayout contentPaneLayout = new GroupLayout(contentPane);
		contentPane.setLayout(contentPaneLayout);
		contentPaneLayout.setHorizontalGroup(
				contentPaneLayout.createParallelGroup()
						.add(contentPaneLayout.createSequentialGroup()
								.addContainerGap()
								.add(resetButton)
								.addPreferredGap(LayoutStyle.RELATED)
								.add(logsButton)
								.addPreferredGap(LayoutStyle.RELATED)
								.add(cancelButton)
								.addPreferredGap(LayoutStyle.UNRELATED)
								.add(saveButton, GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)
								.add(11, 11, 11))
						.add(GroupLayout.TRAILING, mainOptions, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
		);
		contentPaneLayout.setVerticalGroup(
				contentPaneLayout.createParallelGroup()
						.add(contentPaneLayout.createSequentialGroup()
								.add(mainOptions, GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
								.addPreferredGap(LayoutStyle.RELATED)
								.add(contentPaneLayout.createParallelGroup(GroupLayout.BASELINE)
										.add(resetButton)
										.add(logsButton)
										.add(cancelButton)
										.add(saveButton))
								.addContainerGap())
		);
		pack();
		setLocationRelativeTo(getOwner());
	}
}

class DeveloperCodeListener implements DocumentListener {
	JTextField field;

	DeveloperCodeListener(JTextField field) {
		this.field = field;
	}

	public void insertUpdate(DocumentEvent e) {
	}

	public void removeUpdate(DocumentEvent e) {
	}

	public void changedUpdate(DocumentEvent e) {
	}
}
