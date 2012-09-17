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
import java.awt.Desktop;
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
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

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
import org.spoutcraft.launcher.api.util.OperatingSystem;
import org.spoutcraft.launcher.api.util.Utils;
import org.spoutcraft.launcher.entrypoint.SpoutcraftLauncher;

import static org.spoutcraft.launcher.api.util.ResourceUtils.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public class LegacyLoginFrame extends LoginFrame implements ActionListener, KeyListener, WindowListener {
	private static final String MEMORY_ACTION = "memory";
	private static final String VERSION_ACTION = "version";
	private static final String LOGIN_SKIN_2_ACTION = "LoginSkin2";
	private static final String LOGIN_SKIN_1_ACTION = "LoginSkin1";
	private static final String LOGIN_ACTION = "login";
	private static final String USERNAME_ACTION = "username";
	private static final String FORGET_1_ACTION = "Forget1";
	private static final String FORGET_2_ACTION = "Forget2";
	public static final URL spoutcraftIcon = SpoutcraftLauncher.class.getResource("/org/spoutcraft/launcher/resources/icon.png");
	public static final URL spoutcraftLogo = SpoutcraftLauncher.class.getResource("/org/spoutcraft/launcher/resources/spoutcraft.png");
	public static final URL gearIcon = SpoutcraftLauncher.class.getResource("/org/spoutcraft/launcher/resources/gear_icon.png");
	private static final long serialVersionUID = 1L;
	private final JPanel contentPane = new JPanel();
	private final Container loginPane = new Container();
	private final Container offlinePane = new Container();
	public final JProgressBar progressBar;
	private final JPasswordField passwordField;
	private final JComboBox usernameField = new JComboBox();
	private final JButton loginButton = new JButton("Login");
	private final JCheckBox rememberCheckbox = new JCheckBox("Remember");
	private final JButton forgetPlayer1;
	private final JLabel player1Name;
	private long forget1Time = 0;
	private final List<JButton> loginSkin1Image;
	private final JButton forgetPlayer2;
	private final JLabel player2Name;
	private long forget2Time = 0;
	private final List<JButton> loginSkin2Image;
	private final JComboBox version = new JComboBox();
	private final JComboBox memory = new JComboBox();
	private final ForgetThread thread;
	private final JButton options = new JButton();
	private final JLabel optionsLabel = new JLabel("Options:");
	private OptionsMenu optionsMenu = null;

	// Fonts
	private final Font arial11 = new Font("Arial", Font.PLAIN, 11);
	private final Font arial12 = new Font("Arial", Font.PLAIN, 12);
	private final Font arial14 = new Font("Arial", Font.PLAIN, 14);
	private final Font minecraft12;

	public LegacyLoginFrame(Skin parent) {
		super(parent);
		
		Font minecraft;
		try {
			minecraft = Font.createFont(Font.TRUETYPE_FONT, getResourceAsStream("/org/spoutcraft/launcher/resources/minecraft.ttf")).deriveFont(12F);
		} catch (Exception e) {
			e.printStackTrace();
			//Fallback to arial
			minecraft = arial12;
		}
		minecraft12 = minecraft;

		setTitle("Spoutcraft Launcher");
		setIconImage(Toolkit.getDefaultToolkit().getImage(spoutcraftIcon));

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int width, height;
		if (OperatingSystem.getOS() == OperatingSystem.WINDOWS_8) {
			width = 880;
			height = 520;
		} else {
			width = 860;
			height = 500;
		}
		setBounds((dim.width - width) / 2, (dim.height - height) / 2, width, height);
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
		usernameField.setActionCommand(USERNAME_ACTION);
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
		memory.setActionCommand(MEMORY_ACTION);
		populateMemory(memory);
		memory.addActionListener(this);
		
		options.setIcon(new ImageIcon(gearIcon));
		options.setBounds(320, 88, 30, 30);
		options.setFocusable(false);
		options.setContentAreaFilled(false);
		options.setBorderPainted(false);
		options.setActionCommand("Options");
		options.addActionListener(this);
		
		optionsLabel.setFont(arial11);
		optionsLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		optionsLabel.setBounds(216, 90, 100, 30);
		
		player1Name = new JLabel();
		player1Name.setFont(minecraft12);
		
		player2Name = new JLabel();
		player2Name.setFont(minecraft12);

		forgetPlayer1 = new JButton("Forget");
		forgetPlayer1.setFont(arial11);
		forgetPlayer1.setBounds(72, 428, 119, 23);
		forgetPlayer1.setOpaque(false);
		forgetPlayer1.addActionListener(this);
		forgetPlayer1.setVisible(false);
		loginSkin1Image = new ArrayList<JButton>();

		forgetPlayer2 = new JButton("Forget");
		forgetPlayer2.setFont(arial11);
		forgetPlayer2.setBounds(261, 428, 119, 23);
		forgetPlayer2.setOpaque(false);
		forgetPlayer2.addActionListener(this);
		forgetPlayer2.setVisible(false);
		loginSkin2Image = new ArrayList<JButton>();

		int loginid = 0;
		for (String user : getSavedUsernames()) {
			if (hasSavedPassword(user)) {
				loginid++;
				if (loginid == 1) {
					player1Name.setText(getUsername(user));
					forgetPlayer1.setVisible(true);
					ImageUtils.drawCharacter(contentPane, this, getSkinURL(user), 103, 170, loginSkin1Image);
					forgetPlayer1.setActionCommand(FORGET_1_ACTION);
					for (JButton button : loginSkin1Image) {
						button.setActionCommand(LOGIN_SKIN_1_ACTION);
					}
					passwordField.setText(getSavedPassword(user));
					rememberCheckbox.setSelected(true);
				} else if (loginid == 2) {
					player2Name.setText(getUsername(user));
					forgetPlayer2.setVisible(true);
					ImageUtils.drawCharacter(contentPane, this, getSkinURL(user), 293, 170, loginSkin2Image);
					forgetPlayer2.setActionCommand(FORGET_2_ACTION);
					for (JButton button : loginSkin2Image) {
						button.setActionCommand(LOGIN_SKIN_2_ACTION);
					}
				}
				usernameField.addItem(user);
			}
		}
		
		player1Name.setHorizontalAlignment(SwingConstants.CENTER);
		player1Name.setBounds(72, 144, 119, 23);
		player2Name.setHorizontalAlignment(SwingConstants.CENTER);
		player2Name.setBounds(261, 144, 119, 23);

		progressBar = new JProgressBar();
		progressBar.setBounds(30, 100, 400, 23);
		progressBar.setVisible(false);
		progressBar.setStringPainted(true);
		progressBar.setOpaque(true);

		JLabel purchaseAccount = new HyperlinkJLabel("<html><u>Need a Minecraft account?</u></html>", "http://www.minecraft.net/register.jsp");
		purchaseAccount.setHorizontalAlignment(SwingConstants.RIGHT);
		purchaseAccount.setBounds(271, 70, 90, 14);

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
		contentPane.add(forgetPlayer1);
		contentPane.add(player1Name);
		contentPane.add(forgetPlayer2);
		contentPane.add(player2Name);

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
		loginPane.add(options);
		loginPane.add(optionsLabel);
		contentPane.add(loginPane);

		version.addActionListener(this);
		version.setActionCommand(VERSION_ACTION);
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

		int buildNumber = Settings.getLauncherBuild();
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
		thread = new ForgetThread();
		thread.start();
		
		if (Main.isOldLauncher()) {
			showOutdatedWarning();
		}
	}
	private void showOutdatedWarning() {
	    JLabel label = new JLabel();
	    label.setFont(arial12);
	
	    StringBuffer style = new StringBuffer("font-family:" + arial12.getFamily() + ";");
	    style.append("font-weight:" + (arial12.isBold() ? "bold" : "normal") + ";");
	    style.append("font-size:" + arial12.getSize() + "pt;");
	
	    JEditorPane ep = new JEditorPane("text/html", "<html><body style=\"" + style + "\">" 
	            + "Please download our newest launcher from <a href=\"http://get.spout.org/\">http://get.spout.org</a>"
	            + "<br/>This launcher will continue to work for only a short time longer.</body></html>");
	
	    ep.addHyperlinkListener(new HyperlinkListener() {
	        public void hyperlinkUpdate(HyperlinkEvent e) {
	            if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED))
					try {
						Desktop.getDesktop().browse(e.getURL().toURI());
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (URISyntaxException e1) {
						e1.printStackTrace();
					}
	        }
	    });
	    ep.setEditable(false);
	    ep.setBackground(label.getBackground());

	    JOptionPane.showMessageDialog(this, ep);
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
			memory.addItem(String.valueOf(Memory.memoryOptions[0]));
			Settings.setMemory(1); //512 == 1
			memory.setSelectedIndex(0); //1st element
		}
	}

	@Override
	public void disableForm() {
		usernameField.setEnabled(false);
		passwordField.setEnabled(false);
		version.setEnabled(false);
		memory.setEnabled(false);
		rememberCheckbox.setEnabled(false);
		loginButton.setEnabled(false);
		forgetPlayer1.setEnabled(false);
		forgetPlayer2.setEnabled(false);
	}

	@Override
	public void enableForm() {
		usernameField.setEnabled(true);
		passwordField.setEnabled(true);
		if (Settings.getSpoutcraftBuild() != Build.CUSTOM){
			version.setEnabled(true);
		}
		memory.setEnabled(true);
		rememberCheckbox.setEnabled(true);
		loginButton.setEnabled(true);
		forgetPlayer1.setEnabled(true);
		forgetPlayer2.setEnabled(true);
	}

	public void init() {
	}

	public JProgressBar getProgressBar() {
		return progressBar;
	}

	public void actionPerformed(ActionEvent e) {
		//Selected username from dropdown
		if (e.getActionCommand().equals(USERNAME_ACTION)) {
			if (this.hasSavedPassword(usernameField.getSelectedItem().toString())) {
				passwordField.setText(getSavedPassword(usernameField.getSelectedItem().toString()));
			} else {
				passwordField.setText("");
			}
		}
		//Username/Pass login
		else if (e.getActionCommand().equalsIgnoreCase(LOGIN_ACTION)) {
			if (usernameField.getSelectedItem() != null) {
				disableForm();
				doLogin(usernameField.getSelectedItem().toString(), new String(passwordField.getPassword()));
				if (rememberCheckbox.isSelected()) {
					saveUsername(usernameField.getSelectedItem().toString(), new String(passwordField.getPassword()));
				}
			}
		//Login Skin 1
		} else if (e.getActionCommand().equals(LOGIN_SKIN_1_ACTION)) {
			disableForm();
			doLogin(getAccountName(player1Name.getText()));
		//Login Skin 2
		} else if (e.getActionCommand().equals(LOGIN_SKIN_2_ACTION)) {
			disableForm();
			doLogin(getAccountName(player2Name.getText()));
		//Forget 1
		} else if (e.getActionCommand().equals(FORGET_1_ACTION)) {
			//Are you sure?
			if (forgetPlayer1.getText().equals("Forget")) {
				forgetPlayer1.setText("Are you sure?");
				forget1Time = System.currentTimeMillis() + 1000 * 5;
			} else {
				removeAccount(getAccountName(player1Name.getText()));
				forgetPlayer1.setVisible(false);
				forgetPlayer1.setEnabled(false);
				player1Name.setVisible(false);
				for (JButton b : loginSkin1Image) {
					b.setVisible(false);
					b.setEnabled(false);
				}
				writeUsernameList();
			}
		//Forget 2
		} else if (e.getActionCommand().equals(FORGET_2_ACTION)) {
			//Are you sure?
			if (forgetPlayer2.getText().equals("Forget")) {
				forgetPlayer2.setText("Are you sure?");
				forget2Time = System.currentTimeMillis() + 1000 * 5;
			} else {
				removeAccount(getAccountName(player2Name.getText()));
				forgetPlayer2.setVisible(false);
				forgetPlayer2.setEnabled(false);
				player2Name.setVisible(false);
				for (JButton b : loginSkin2Image) {
					b.setVisible(false);
					b.setEnabled(false);
				}
				writeUsernameList();
			}
		//Version
		} else if (e.getActionCommand().equals(VERSION_ACTION) && Settings.getSpoutcraftBuild() != Build.CUSTOM) {
			Build build = version.getSelectedIndex() == 0 ? Build.RECOMMENDED : Build.DEV;
			if (build != Settings.getSpoutcraftBuild()) {
				Settings.setSpoutcraftBuild(build);
				Launcher.getGameUpdater().onSpoutcraftBuildChange();
			}
		//Memory
		} else if (e.getActionCommand().equals(MEMORY_ACTION)) {
			int index = memory.getSelectedIndex();
			Settings.setMemory(Memory.memoryOptions[index].getSettingsId());
		} else if (e.getActionCommand().equals("Options")) {
			if (optionsMenu == null || !optionsMenu.isVisible()) {
				optionsMenu = new OptionsMenu();
				optionsMenu.setAlwaysOnTop(true);
				optionsMenu.setVisible(true);
			}
		}
		if (loginButton.isEnabled()) {
			Settings.getYAML().save();
		}
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		if (loginButton.isEnabled() && e.getKeyCode() == KeyEvent.VK_ENTER) {
			disableForm();
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
				enableForm();
				break;
			case ACCOUNT_MIGRATED:
				JOptionPane.showMessageDialog(getParent(), "Please use your email address instead of your username.", "Account Migrated!", JOptionPane.WARNING_MESSAGE);
				removeAccount(usernameField.getSelectedItem().toString());
				enableForm();
				break;
			case USER_NOT_PREMIUM:
				JOptionPane.showMessageDialog(getParent(), "You purchase a Minecraft account to play");
				enableForm();
				break;
			case MINECRAFT_NETWORK_DOWN:
				if (!canPlayOffline()) {
					JOptionPane.showMessageDialog(getParent(), "Unable to authenticate account with minecraft.net");
				} else {
					int result = JOptionPane.showConfirmDialog(getParent(), "Would you like to run in offline mode?", "Unable to connect to minecraft.net", JOptionPane.YES_NO_OPTION);
					if (result == JOptionPane.YES_OPTION) {
						Launcher.getGameLauncher().runGame(Launcher.getGameUpdater().getMinecraftUser(), "", "");
					} else {
						enableForm();
					}
				}
				break;

			case PERMISSION_DENIED:
				JOptionPane.showMessageDialog(getParent(), "Ensure Spoutcraft is whitelisted with any antivirus applications.", "Permission Denied!", JOptionPane.WARNING_MESSAGE);
				enableForm();
				break;
		case GAME_LAUNCH:
			thread.interrupt();
			break;
		case SUCESSFUL_LOGIN:
			break;
		default:
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

	private class ForgetThread extends Thread {
		public ForgetThread() {
			super("Forget Thread");
			setDaemon(true);
		}

		@Override
		public void run() {
			while (!this.isInterrupted()) {
				if (forgetPlayer1.isVisible() && !forgetPlayer1.getText().equals("Forget")) {
					if (forget1Time < System.currentTimeMillis()) {
						forgetPlayer1.setText("Forget");
					}
				}
				if (forgetPlayer2.isVisible() && !forgetPlayer2.getText().equals("Forget")) {
					if (forget2Time < System.currentTimeMillis()) {
						forgetPlayer2.setText("Forget");
					}
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					return;
				}
			}
		}
	}
}