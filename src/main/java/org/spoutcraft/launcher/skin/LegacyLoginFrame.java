/*
 * This file is part of Spoutcraft Launcher.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spoutcraft Launcher is licensed under the SpoutDev License Version 1.
 *
 * Spoutcraft Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spoutcraft Launcher is distributed in the hope that it will be useful,
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
package org.spoutcraft.launcher.skin;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.spoutcraft.launcher.Main;
import org.spoutcraft.launcher.Memory;
import org.spoutcraft.launcher.Settings;
import org.spoutcraft.launcher.api.Build;
import org.spoutcraft.launcher.api.Event;
import org.spoutcraft.launcher.api.Launcher;
import org.spoutcraft.launcher.api.skin.Skin;
import org.spoutcraft.launcher.api.skin.gui.HyperlinkJLabel;
import org.spoutcraft.launcher.api.skin.gui.LoginFrame;
import org.spoutcraft.launcher.api.util.ImageUtils;
import org.spoutcraft.launcher.api.util.Utils;

public class LegacyLoginFrame extends LoginFrame implements ActionListener, KeyListener, WindowListener {
	public static final URL spoutcraftIcon = Main.class.getResource("resources/icon.png");
	public static final URL spoutcraftLogo = Main.class.getResource("resources/spoutcraft.png");
	private static final long serialVersionUID = 1797546961340465149L;
	private JPanel contentPane = new JPanel();
	private Container loginPane = new Container();
	private Container offlinePane = new Container();
	public JProgressBar progressBar;
	private JPasswordField passwordField;
	private JComboBox usernameField = new JComboBox();
	private JButton loginButton = new JButton("Login");
	private JCheckBox rememberCheckbox = new JCheckBox("Remember");
	private JButton loginSkin1;
	private List<JButton> loginSkin1Image;
	private JButton loginSkin2;
	private List<JButton> loginSkin2Image;
	private JComboBox version = new JComboBox();
	private JComboBox memory = new JComboBox();

	// Fonts
	private Font arial11 = new Font("Arial", Font.PLAIN, 11);
	private Font arial12 = new Font("Arial", Font.PLAIN, 12);
	private Font arial14 = new Font("Arial", Font.PLAIN, 14);

	public LegacyLoginFrame(Skin parent) {
		super(parent);
		setTitle("Spoutcraft Launcher");
		setIconImage(Toolkit.getDefaultToolkit().getImage(spoutcraftIcon));

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((dim.width - 860) / 2, (dim.height - 500) / 2, 860, 500);
		setResizable(false);

		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		loginPane.setBounds(473, 362, 372, 99);

		loginButton.setFont(arial11);
		loginButton.setBounds(272, 13, 86, 23);
		loginButton.setOpaque(false);

		loginButton.addActionListener(this);
		loginButton.setEnabled(false);

		usernameField.setFont(arial11);
		usernameField.addActionListener(this);
		usernameField.setOpaque(false);

		JLabel lblLogo = new JLabel("");
		lblLogo.setBounds(8, 0, 294, 99);
		lblLogo.setIcon(new ImageIcon(spoutcraftLogo));

		JLabel lblMinecraftUsername = new JLabel("Minecraft Username: ");
		lblMinecraftUsername.setFont(arial11);
		lblMinecraftUsername.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMinecraftUsername.setBounds(-17, 17, 150, 14);

		JLabel lblPassword = new JLabel("Password: ");
		lblPassword.setFont(arial11);
		lblPassword.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPassword.setBounds(33, 42, 100, 20);

		passwordField = new JPasswordField();
		passwordField.setFont(arial11);
		passwordField.setBounds(143, 42, 119, 22);

		JLabel versionLabel = new JLabel("Version: ");
		versionLabel.setFont(arial11);
		versionLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		versionLabel.setBounds(-17, 72, 150, 14);

		version.setFont(arial11);
		version.addItem("Recommended");
		version.addItem("Latest");
		version.setBounds(143, 68, 119, 22);
		version.setEditable(false);

		JLabel memoryLabel = new JLabel("Memory: ");
		memoryLabel.setFont(arial11);
		memoryLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		memoryLabel.setBounds(-17, 98, 150, 14);

		memory.setFont(arial11);
		memory.setBounds(143, 94, 119, 22);
		memory.setEditable(false);
		memory.setActionCommand("memory");
		populateMemory(memory);
		memory.addActionListener(this);

		loginSkin1 = new JButton("Login as Player");
		loginSkin1.setFont(arial11);
		loginSkin1.setBounds(72, 428, 119, 23);
		loginSkin1.setOpaque(false);
		loginSkin1.addActionListener(this);
		loginSkin1.setVisible(false);
		loginSkin1Image = new ArrayList<JButton>();

		loginSkin2 = new JButton("Login as Player");
		loginSkin2.setFont(arial11);
		loginSkin2.setBounds(261, 428, 119, 23);
		loginSkin2.setOpaque(false);
		loginSkin2.addActionListener(this);
		loginSkin2.setVisible(false);
		loginSkin2Image = new ArrayList<JButton>();

		int loginid = 0;
		for (String user : getSavedUsernames()) {
			if (hasSavedPassword(user)) {
				loginid++;
				if (loginid == 1) {
					loginSkin1.setText(getUsername(user));
					loginSkin1.setVisible(true);
					ImageUtils.drawCharacter(contentPane, this, getSkinURL(user), 103, 170, loginSkin1Image);
					loginSkin1.setActionCommand("LoginSkin1");
					for (JButton button : loginSkin1Image) {
						button.setActionCommand("LoginSkin1");
					}
					passwordField.setText(getSavedPassword(user));
					rememberCheckbox.setSelected(true);
				} else if (loginid == 2) {
					loginSkin2.setText(getUsername(user));
					loginSkin2.setVisible(true);
					ImageUtils.drawCharacter(contentPane, this, getSkinURL(user), 293, 170, loginSkin2Image);
					loginSkin2.setActionCommand("LoginSkin2");
					for (JButton button : loginSkin2Image) {
						button.setActionCommand("LoginSkin2");
					}
				}
				usernameField.addItem(user);
			}
		}

		progressBar = new JProgressBar();
		progressBar.setBounds(30, 100, 400, 23);
		progressBar.setVisible(false);
		progressBar.setStringPainted(true);
		progressBar.setOpaque(true);

		JLabel purchaseAccount = new HyperlinkJLabel("<html><u>Need a Minecraft account?</u></html>", "http://www.minecraft.net/register.jsp");
		purchaseAccount.setHorizontalAlignment(SwingConstants.RIGHT);
		purchaseAccount.setBounds(250, 70, 111, 14);

		purchaseAccount.setText("<html><u>Need an account?</u></html>");
		purchaseAccount.setFont(arial11);
		purchaseAccount.setForeground(new Color(0, 0, 255));
		usernameField.setBounds(143, 14, 119, 25);
		rememberCheckbox.setFont(arial11);

		rememberCheckbox.setOpaque(false);

		final JTextPane editorPane = new JTextPane();
		editorPane.setContentType("text/html");

		editorPane.setEditable(false);
		editorPane.setOpaque(false);
		editorPane.setFont(arial11);

		AsyncRSSFeed rss = new AsyncRSSFeed(editorPane);
		if (getSavedUsernames().size() > 0) {
			rss.setUser(getSavedUsernames().get(0));
		}
		rss.execute();

		JLabel trans2;

		JScrollPane scrollPane = new JScrollPane(editorPane);
		scrollPane.setBounds(473, 11, 372, 320);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);

		editorPane.setCaretPosition(0);
		trans2 = new JLabel();
		trans2.setBackground(new Color(229, 246, 255, 100));
		trans2.setOpaque(true);
		trans2.setBounds(473, 11, 372, 320);

		JLabel login = new JLabel();
		login.setBackground(new Color(255, 255, 255, 120));
		login.setOpaque(true);
		login.setBounds(473, 342, 372, 119);

		JLabel trans;
		trans = new JLabel();
		trans.setBackground(new Color(229, 246, 255, 60));
		trans.setOpaque(true);
		trans.setBounds(0, 0, 854, 480);

		usernameField.getEditor().addActionListener(this);
		passwordField.addKeyListener(this);
		rememberCheckbox.addKeyListener(this);

		usernameField.setEditable(true);
		contentPane.setLayout(null);
		rememberCheckbox.setBounds(272, 41, 86, 23);
		contentPane.add(lblLogo);
		contentPane.add(loginSkin1);
		contentPane.add(loginSkin2);

		loginPane.setBounds(473, 342, 372, 119);
		loginPane.add(lblPassword);
		loginPane.add(lblMinecraftUsername);
		loginPane.add(passwordField);
		loginPane.add(usernameField);
		loginPane.add(loginButton);
		loginPane.add(rememberCheckbox);
		loginPane.add(purchaseAccount);
		loginPane.add(version);
		loginPane.add(versionLabel);
		loginPane.add(memory);
		loginPane.add(memoryLabel);
		contentPane.add(loginPane);

		version.addActionListener(this);
		version.setActionCommand("Version");
		if (Settings.getSpoutcraftBuild() == Build.RECOMMENDED) {
			version.setSelectedIndex(0);
		} else if (Settings.getSpoutcraftBuild() == Build.DEV) {
			version.setSelectedIndex(1);
		} else {
			version.removeAllItems();
			version.addItem("b" + Settings.getSpoutcraftSelectedBuild());
			version.setSelectedIndex(0);
			version.setEnabled(false);
		}

		JLabel offlineMessage = new JLabel("Could not connect to minecraft.net");
		offlineMessage.setFont(arial14);
		offlineMessage.setBounds(25, 40, 217, 17);

		JButton tryAgain = new JButton("Try Again");
		tryAgain.setOpaque(false);
		tryAgain.setFont(arial12);
		tryAgain.setBounds(257, 20, 100, 25);

		JButton offlineMode = new JButton("Offline Mode");
		offlineMode.setOpaque(false);
		offlineMode.setFont(arial12);
		offlineMode.setBounds(257, 52, 100, 25);

		offlinePane.setBounds(473, 362, 372, 99);
		offlinePane.add(tryAgain);
		offlinePane.add(offlineMode);
		offlinePane.add(offlineMessage);
		offlinePane.setVisible(false);
		contentPane.add(offlinePane);

		int buildNumber = Settings.getLauncherSelectedBuild();
		JLabel build = new JLabel("Launcher Build: " + (buildNumber == -1 ? "Custom" : "b" + buildNumber));
		build.setFont(arial11);
		build.setOpaque(false);
		build.setBounds(3, 460, 125, 12);

		contentPane.add(build);
		contentPane.add(scrollPane);
		contentPane.add(trans2);
		contentPane.add(login);
		contentPane.add(trans);
		contentPane.add(progressBar);

		final JLabel background = new JLabel("Loading...");
		background.setVerticalAlignment(SwingConstants.CENTER);
		background.setHorizontalAlignment(SwingConstants.CENTER);
		background.setBounds(0, 0, 854, 480);
		contentPane.add(background);

		File cacheDir = new File(Utils.getWorkingDirectory(), "cache");
		cacheDir.mkdirs();
		File backgroundImage = new File(cacheDir, "launcher_background.jpg");
		(new BackgroundImageWorker(backgroundImage, background)).execute();

		Vector<Component> order = new Vector<Component>(6);
		order.add(usernameField.getEditor().getEditorComponent());
		order.add(passwordField);
		order.add(rememberCheckbox);
		order.add(loginButton);
		order.add(version);
		order.add(memory);

		setFocusTraversalPolicy(new SpoutFocusTraversalPolicy(order));

		addWindowListener(this);

		loginButton.setEnabled(true);
	}

	@SuppressWarnings("restriction")
	private void populateMemory(JComboBox memory) {
		long maxMemory = 1024;
		String architecture = System.getProperty("sun.arch.data.model", "32");
		boolean bit64 = architecture.equals("64");

		try {
			OperatingSystemMXBean osInfo = ManagementFactory.getOperatingSystemMXBean();
			if (osInfo instanceof com.sun.management.OperatingSystemMXBean) {
				maxMemory = ((com.sun.management.OperatingSystemMXBean) osInfo).getTotalPhysicalMemorySize() / 1024 / 1024;
			}
		} catch (Throwable t) { }
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

		if (memoryOption < 0 || memoryOption > Memory.memoryOptions.length){
			memoryOption = 0;
		}
		if (Memory.memoryOptions[memoryOption].getMemoryMB() > maxMemory) {
			memoryOption = 0;
		}

		try {
			Settings.setMemory(memoryOption);
			memory.setSelectedIndex(Memory.getMemoryIndexFromId(memoryOption));
		} catch (IllegalArgumentException e) {
			memory.removeAllItems();
			memory.addItem(Memory.memoryOptions[0]);
			Settings.setMemory(1); //512 == 1
			memory.setSelectedIndex(0); //1st element
		}
	}

	public void disable() {
		usernameField.setEnabled(false);
		passwordField.setEnabled(false);
		version.setEnabled(false);
		memory.setEnabled(false);
		rememberCheckbox.setEnabled(false);
		loginButton.setEnabled(false);
		loginSkin1.setEnabled(false);
		loginSkin2.setEnabled(false);
	}

	public void enable() {
		usernameField.setEnabled(true);
		passwordField.setEnabled(true);
		if (Settings.getSpoutcraftBuild() != Build.CUSTOM){
			version.setEnabled(true);
		}
		memory.setEnabled(true);
		rememberCheckbox.setEnabled(true);
		loginButton.setEnabled(true);
		loginSkin1.setEnabled(true);
		loginSkin2.setEnabled(true);
	}

	public void init() {
	}

	public JProgressBar getProgressBar() {
		return progressBar;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equalsIgnoreCase("login")) {
			if (usernameField.getSelectedItem() != null) {
				disable();
				doLogin(usernameField.getSelectedItem().toString(), new String(passwordField.getPassword()));
				if (rememberCheckbox.isSelected()) {
					saveUsername(usernameField.getSelectedItem().toString(), new String(passwordField.getPassword()));
				}
			}
		} else if (e.getActionCommand().equals(loginSkin1.getActionCommand())) {
			disable();
			doLogin(getAccountName(loginSkin1.getText()));
		} else if (e.getActionCommand().equals(loginSkin2.getActionCommand())) {
			disable();
			doLogin(getAccountName(loginSkin2.getText()));
		} else if (e.getActionCommand().equals("Version") && Settings.getSpoutcraftBuild() != Build.CUSTOM) {
			Build build = version.getSelectedIndex() == 0 ? Build.RECOMMENDED : Build.DEV;
			if (build != Settings.getSpoutcraftBuild()) {
				Settings.setSpoutcraftBuild(build);
				Launcher.getGameUpdater().onSpoutcraftBuildChange();
			}
		} else if (e.getActionCommand().equals("memory")) {
			int index = memory.getSelectedIndex();
			Settings.setMemory(Memory.memoryOptions[index].getSettingsId());
		}
		if (loginButton.isEnabled()) {
			Settings.getSettings().save();
		}
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		if (loginButton.isEnabled() && e.getKeyCode() == KeyEvent.VK_ENTER) {
			disable();
			doLogin(usernameField.getSelectedItem().toString(), new String(passwordField.getPassword()));
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void onEvent(Event event) {
		switch (event) {
			case BAD_LOGIN:
				JOptionPane.showMessageDialog(getParent(), "Incorrect username/password combination");
				enable();
				break;
			case ACCOUNT_MIGRATED:
				JOptionPane.showMessageDialog(getParent(), "Please use your email address instead of your username.", "Account Migrated!", JOptionPane.WARNING_MESSAGE);
				enable();
				break;
			case MINECRAFT_NETWORK_DOWN:
				if (!canPlayOffline()) {
					JOptionPane.showMessageDialog(getParent(), "Unable to authenticate account with minecraft.net");
				} else {
					int result = JOptionPane.showConfirmDialog(getParent(), "Would you like to run in offline mode?", "Unable to connect to minecraft.net", JOptionPane.YES_NO_OPTION);
					if (result == JOptionPane.YES_OPTION) {
						Launcher.getGameLauncher().runGame(Launcher.getGameUpdater().getMinecraftUser(), "", "");
					} else {
						enable();
					}
				}
				break;
			case USER_NOT_PREMIUM:
				JOptionPane.showMessageDialog(getParent(), "You purchase a Minecraft account to play");
				enable();
				break;
			case PERMISSION_DENIED:
				JOptionPane.showMessageDialog(getParent(), "Ensure Spoutcraft is whitelisted with any antivirus applications.", "Permission Denied!", JOptionPane.WARNING_MESSAGE);
				enable();
				break;
		}
	}

	public void stateChanged(String status, float progress) {
		int intProgress = Math.round(progress);

		progressBar.setValue(intProgress);
		if (status.length() > 60) {
			status = status.substring(0, 60) + "...";
		}
		progressBar.setString(intProgress + "% " + status);
	}

	public void windowOpened(WindowEvent e) {
	}

	public void windowClosing(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}
}
