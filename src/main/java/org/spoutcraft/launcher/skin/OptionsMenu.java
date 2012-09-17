package org.spoutcraft.launcher.skin;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.URL;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
import org.spoutcraft.launcher.Channel;
import org.spoutcraft.launcher.Memory;
import org.spoutcraft.launcher.Proxy;
import org.spoutcraft.launcher.Settings;
import org.spoutcraft.launcher.entrypoint.SpoutcraftLauncher;
import org.spoutcraft.launcher.rest.Versions;

@SuppressWarnings({ "restriction", "rawtypes", "unchecked" })
public class OptionsMenu extends JDialog implements ActionListener{
	private static final long serialVersionUID = 1L;
	private static final URL spoutcraftIcon = SpoutcraftLauncher.class.getResource("/org/spoutcraft/launcher/resources/icon.png");
	private static final String CANCEL_ACTION = "cancel";
	private static final String RESET_ACTION = "reset";
	private static final String SAVE_ACTION = "save";
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
	private JLabel proxyUsername;
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
	private JButton resetButton;
	private JButton cancelButton;
	private JButton saveButton;
	public OptionsMenu() {
		initComponents();
		
		setTitle("Launcher Options");
		setIconImage(Toolkit.getDefaultToolkit().getImage(spoutcraftIcon));
		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		populateMemory(memory);

		cancelButton.addActionListener(this);
		cancelButton.setActionCommand(CANCEL_ACTION);
		
		resetButton.addActionListener(this);
		resetButton.setActionCommand(RESET_ACTION);
		
		saveButton.addActionListener(this);
		saveButton.setActionCommand(SAVE_ACTION);
		
		developerCode.setText(Settings.getDeveloperCode());
		developerCode.getDocument().addDocumentListener(new DeveloperCodeListener(developerCode));
		
		Settings.setSpoutcraftChannel(populateChannelVersion(spoutcraftVersion, Settings.getSpoutcraftChannel().type()));
		Settings.setLauncherChannel(populateChannelVersion(launcherVersion, Settings.getLauncherChannel().type()));
		
		for (String version : Versions.getMinecraftVersions()) {
			this.minecraftVersion.addItem(version);
		}
	}

	private Channel populateChannelVersion(JComboBox version, int selection) {
		version.addItem("Stable");
		version.addItem("Beta");
		if (Settings.getDeveloperCode().length() > 0) {
			version.addItem("Dev");
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

	public void actionPerformed(ActionEvent e) {
		action(e.getActionCommand());
	}

	private void action(String command) {
		if (command.equals(CANCEL_ACTION)) {
			closeForm();
		} else if (command.equals(RESET_ACTION)) {
			
		} else if (command.equals(SAVE_ACTION)) {
			Settings.setLauncherChannel(Channel.getType(launcherVersion.getSelectedIndex()));
			Settings.setSpoutcraftChannel(Channel.getType(spoutcraftVersion.getSelectedIndex()));
			Settings.setMemory(Memory.memoryOptions[memory.getSelectedIndex()].getSettingsId());
			Settings.setDebugMode(debugMode.isSelected());
			Settings.setIgnoreMD5(md5Checkbox.isSelected());
			Settings.setProxyHost(this.proxyHost.getText());
			Settings.setProxyPort(this.proxyPort.getText());
			Settings.setProxyUsername(this.proxyUsername.getText());
			Settings.setProxyPassword(this.proxyPass.getPassword());
			Proxy proxy = new Proxy();
			proxy.setHost(Settings.getProxyHost());
			proxy.setPort(Settings.getProxyPort());
			proxy.setUser(Settings.getProxyUsername());
			proxy.setPass(Settings.getProxyPassword().toCharArray());
			proxy.setup();
			Settings.getYAML().save();
			closeForm();
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
		proxyPane = new JPanel();
		proxyHostLabel = new JLabel();
		proxyPortLabel = new JLabel();
		proxyUsername = new JLabel();
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

				// JFormDesigner evaluation mark

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
									.add(memory, GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE))
								.add(gamePaneLayout.createSequentialGroup()
									.add(gamePaneLayout.createParallelGroup()
										.add(minecraftVersionLabel)
										.add(spoutcraftVersionLabel))
									.addPreferredGap(LayoutStyle.RELATED)
									.add(gamePaneLayout.createParallelGroup()
										.add(spoutcraftVersion, GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
										.add(minecraftVersion, GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE))))
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
							.addContainerGap(113, Short.MAX_VALUE))
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
				proxyUsername.setText("Username:");
				proxyUsername.setFont(new Font("Arial", Font.PLAIN, 11));

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
								.add(proxyUsername)
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
								.add(proxyUsername)
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

		//---- resetButton ----
		resetButton.setText("Reset to Defaults");

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
