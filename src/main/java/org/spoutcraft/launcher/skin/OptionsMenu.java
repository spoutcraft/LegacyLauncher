package org.spoutcraft.launcher.skin;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.URL;
 
import javax.swing.*;

import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
import org.spoutcraft.launcher.LauncherBuild;
import org.spoutcraft.launcher.Memory;
import org.spoutcraft.launcher.Proxy;
import org.spoutcraft.launcher.Settings;
import org.spoutcraft.launcher.entrypoint.SpoutcraftLauncher;

@SuppressWarnings({"rawtypes", "unchecked"})
public class OptionsMenu extends JDialog implements ActionListener{
	private static final long serialVersionUID = 1L;
	private static final URL spoutcraftIcon = SpoutcraftLauncher.class.getResource("/org/spoutcraft/launcher/resources/icon.png");
	private static final String CANCEL_ACTION = "cancel";
	private static final String RESET_ACTION = "reset";
	private static final String SAVE_ACTION = "save";
	private JLabel launcherLabel;
	private JLabel spoutcraftLabel;
	private JSeparator separator1;
	private JComboBox launcherVersion;
	private JLabel launcherVersionLabel;
	private JLabel spoutcraftVersionLabel;
	private JLabel memoryLabel;
	private JComboBox memoryCombo;
	private JLabel debugLabel;
	private JCheckBox debugMode;
	private JLabel proxyLabel;
	private JSeparator separator2;
	private JLabel proxyUsername;
	private JLabel proxyHostLabel;
	private JTextField proxyHost;
	private JLabel proxyPortLabel;
	private JTextField proxyPort;
	private JTextField textField1;
	private JLabel passwordLabel;
	private JPasswordField proxyPassword;
	private JComboBox spoutcraftVersion;
	private JSeparator separator3;
	private JButton cancelButton;
	private JButton resetButton;
	private JButton saveButton;
	public OptionsMenu() {
		initComponents();
		
		setTitle("Launcher Options");
		setIconImage(Toolkit.getDefaultToolkit().getImage(spoutcraftIcon));
		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}

	private void initComponents() {
		launcherLabel = new JLabel();
		spoutcraftLabel = new JLabel();
		separator1 = new JSeparator();
		launcherVersion = new JComboBox();
		launcherVersionLabel = new JLabel();
		spoutcraftVersionLabel = new JLabel();
		memoryLabel = new JLabel();
		memoryCombo = new JComboBox();
		debugLabel = new JLabel();
		debugMode = new JCheckBox();
		proxyLabel = new JLabel();
		separator2 = new JSeparator();
		proxyUsername = new JLabel();
		proxyHostLabel = new JLabel();
		proxyHost = new JTextField();
		proxyPortLabel = new JLabel();
		proxyPort = new JTextField();
		textField1 = new JTextField();
		passwordLabel = new JLabel();
		proxyPassword = new JPasswordField();
		spoutcraftVersion = new JComboBox();
		separator3 = new JSeparator();
		cancelButton = new JButton();
		resetButton = new JButton();
		saveButton = new JButton();

		//======== this ========
		setBackground(Color.white);
		Container contentPane = getContentPane();

		//---- launcherLabel ----
		launcherLabel.setText("Launcher Options:");
		launcherLabel.setFont(new Font("Arial", Font.BOLD, 12));

		//---- spoutcraftLabel ----
		spoutcraftLabel.setText("Spoutcraft Options:");
		spoutcraftLabel.setFont(new Font("Arial", Font.BOLD, 12));

		//---- launcherVersionLabel ----
		launcherVersionLabel.setText("Version:");
		launcherVersionLabel.setBackground(Color.white);
		
		//---- launcherVersion ----
		launcherVersion.setToolTipText("<html>Stable builds have been extensively tested and are for public use<br/>"+
										"Beta builds are less-buggy builds created on a weekly or biweekly schedule<br/>"+
										"Latest builds are very new, with little to no user testing and may be unstable<br/>"+
										"<br/>If you're not sure which to use, select 'Stable'.");
		launcherVersion.addItem("Stable");
		launcherVersion.addItem("Beta");
		launcherVersion.addItem("Latest");
		launcherVersion.setSelectedIndex(Settings.getLauncherChannel().type());

		//---- spoutcraftVersionLabel ----
		spoutcraftVersionLabel.setText("Version:");
		spoutcraftVersionLabel.setFont(new Font("Arial", Font.PLAIN, 11));

		//---- memoryLabel ----
		memoryLabel.setText("Memory:");
		memoryLabel.setBackground(Color.white);
		
		//---- memoryCombo ----
		populateMemory(this.memoryCombo);

		//---- debugLabel ----
		debugLabel.setText("Debug Mode:");
		debugLabel.setBackground(Color.white);

		//---- debugMode ----
		debugMode.setBackground(Color.white);
		debugMode.setSelected(Settings.isDebugMode());

		//---- proxyLabel ----
		proxyLabel.setText("Proxy Options:");
		proxyLabel.setFont(new Font("Arial", Font.BOLD, 12));

		//---- separator2 ----
		separator2.setOrientation(SwingConstants.VERTICAL);

		//---- proxyUsername ----
		proxyUsername.setText("Username:");
		proxyUsername.setFont(new Font("Arial", Font.PLAIN, 11));

		//---- proxyHostLabel ----
		proxyHostLabel.setText("Proxy Host:");
		proxyHostLabel.setFont(new Font("Arial", Font.PLAIN, 11));

		//---- proxyHost ----
		proxyHost.setFont(new Font("Arial", Font.PLAIN, 11));

		//---- proxyPortLabel ----
		proxyPortLabel.setText("Proxy Port:");
		proxyPortLabel.setFont(new Font("Arial", Font.PLAIN, 11));

		//---- proxyPort ----
		proxyPort.setFont(new Font("Arial", Font.PLAIN, 11));

		//---- textField1 ----
		textField1.setFont(new Font("Arial", Font.PLAIN, 11));

		//---- passwordLabel ----
		passwordLabel.setText("Password:");
		passwordLabel.setFont(new Font("Arial", Font.PLAIN, 11));

		//---- passwordField1 ----
		proxyPassword.setFont(new Font("Arial", Font.PLAIN, 11));

		//---- cancelButton ----
		cancelButton.setText("Cancel");
		cancelButton.setFont(new Font("Arial", Font.PLAIN, 12));
		cancelButton.addActionListener(this);
		cancelButton.setActionCommand(CANCEL_ACTION);

		//---- resetButton ----
		resetButton.setText("Reset to Defaults");
		resetButton.setFont(new Font("Arial", Font.PLAIN, 12));
		resetButton.addActionListener(this);
		resetButton.setActionCommand(RESET_ACTION);

		//---- saveButton ----
		saveButton.setText("Save");
		saveButton.setFont(new Font("Arial", Font.PLAIN, 12));
		saveButton.addActionListener(this);
		saveButton.setActionCommand(SAVE_ACTION);

		GroupLayout contentPaneLayout = new GroupLayout(contentPane);
		contentPane.setLayout(contentPaneLayout);
		contentPaneLayout.setHorizontalGroup(
			contentPaneLayout.createParallelGroup()
				.add(contentPaneLayout.createSequentialGroup()
					.add(contentPaneLayout.createParallelGroup(GroupLayout.TRAILING, false)
						.add(separator1)
						.add(contentPaneLayout.createSequentialGroup()
							.addContainerGap()
							.add(contentPaneLayout.createParallelGroup()
								.add(contentPaneLayout.createSequentialGroup()
									.add(contentPaneLayout.createParallelGroup()
										.add(launcherLabel)
										.add(contentPaneLayout.createSequentialGroup()
											.add(memoryLabel)
											.addPreferredGap(LayoutStyle.RELATED)
											.add(memoryCombo, GroupLayout.PREFERRED_SIZE, 93, GroupLayout.PREFERRED_SIZE))
										.add(contentPaneLayout.createSequentialGroup()
											.add(debugLabel)
											.addPreferredGap(LayoutStyle.RELATED)
											.add(debugMode))
										.add(contentPaneLayout.createSequentialGroup()
											.add(launcherVersionLabel)
											.addPreferredGap(LayoutStyle.RELATED)
											.add(launcherVersion, GroupLayout.PREFERRED_SIZE, 96, GroupLayout.PREFERRED_SIZE)))
									.addPreferredGap(LayoutStyle.RELATED)
									.add(separator2, GroupLayout.PREFERRED_SIZE, 11, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(LayoutStyle.RELATED)
									.add(contentPaneLayout.createParallelGroup()
										.add(proxyUsername)
										.add(proxyPortLabel)
										.add(proxyHostLabel)
										.add(passwordLabel))
									.addPreferredGap(LayoutStyle.UNRELATED)
									.add(contentPaneLayout.createParallelGroup()
										.add(GroupLayout.TRAILING, proxyPassword, GroupLayout.PREFERRED_SIZE, 124, GroupLayout.PREFERRED_SIZE)
										.add(GroupLayout.TRAILING, textField1)
										.add(GroupLayout.TRAILING, proxyPort, GroupLayout.PREFERRED_SIZE, 127, GroupLayout.PREFERRED_SIZE)
										.add(GroupLayout.TRAILING, proxyHost)))
								.add(contentPaneLayout.createSequentialGroup()
									.add(contentPaneLayout.createParallelGroup()
										.add(spoutcraftLabel)
										.add(contentPaneLayout.createSequentialGroup()
											.add(spoutcraftVersionLabel)
											.addPreferredGap(LayoutStyle.RELATED)
											.add(spoutcraftVersion, GroupLayout.PREFERRED_SIZE, 96, GroupLayout.PREFERRED_SIZE))
										.add(contentPaneLayout.createSequentialGroup()
											.add(160, 160, 160)
											.add(proxyLabel)))
									.add(110, 110, 110)))
							.add(2, 2, 2))
						.add(GroupLayout.LEADING, separator3))
					.add(0, 0, Short.MAX_VALUE))
				.add(GroupLayout.TRAILING, contentPaneLayout.createSequentialGroup()
					.addContainerGap()
					.add(resetButton, GroupLayout.PREFERRED_SIZE, 137, GroupLayout.PREFERRED_SIZE)
					.add(18, 18, 18)
					.add(cancelButton, GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
					.add(18, 18, 18)
					.add(saveButton, GroupLayout.PREFERRED_SIZE, 79, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		contentPaneLayout.setVerticalGroup(
			contentPaneLayout.createParallelGroup()
				.add(contentPaneLayout.createSequentialGroup()
					.add(4, 4, 4)
					.add(contentPaneLayout.createParallelGroup()
						.add(contentPaneLayout.createSequentialGroup()
							.add(contentPaneLayout.createParallelGroup()
								.add(contentPaneLayout.createSequentialGroup()
									.add(launcherLabel)
									.add(contentPaneLayout.createParallelGroup(GroupLayout.BASELINE)
										.add(launcherVersionLabel)
										.add(launcherVersion, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
									.addPreferredGap(LayoutStyle.UNRELATED)
									.add(contentPaneLayout.createParallelGroup(GroupLayout.BASELINE)
										.add(memoryLabel)
										.add(memoryCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
									.addPreferredGap(LayoutStyle.RELATED)
									.add(contentPaneLayout.createParallelGroup(GroupLayout.LEADING, false)
										.add(debugMode, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.add(debugLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
								.add(contentPaneLayout.createSequentialGroup()
									.add(proxyLabel)
									.addPreferredGap(LayoutStyle.RELATED)
									.add(contentPaneLayout.createParallelGroup(GroupLayout.BASELINE)
										.add(proxyHostLabel, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
										.add(proxyHost, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
									.addPreferredGap(LayoutStyle.RELATED)
									.add(contentPaneLayout.createParallelGroup(GroupLayout.BASELINE)
										.add(proxyPortLabel)
										.add(proxyPort, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
									.addPreferredGap(LayoutStyle.RELATED)
									.add(contentPaneLayout.createParallelGroup(GroupLayout.BASELINE)
										.add(proxyUsername)
										.add(textField1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
							.addPreferredGap(LayoutStyle.RELATED)
							.add(contentPaneLayout.createParallelGroup(GroupLayout.BASELINE)
								.add(passwordLabel)
								.add(proxyPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
						.add(separator2, GroupLayout.PREFERRED_SIZE, 133, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(LayoutStyle.RELATED)
					.add(separator1, GroupLayout.PREFERRED_SIZE, 2, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(LayoutStyle.RELATED)
					.add(spoutcraftLabel)
					.addPreferredGap(LayoutStyle.RELATED)
					.add(contentPaneLayout.createParallelGroup(GroupLayout.BASELINE)
						.add(spoutcraftVersionLabel)
						.add(spoutcraftVersion, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(LayoutStyle.RELATED)
					.add(separator3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(LayoutStyle.UNRELATED)
					.add(contentPaneLayout.createParallelGroup(GroupLayout.BASELINE)
						.add(saveButton)
						.add(resetButton)
						.add(cancelButton))
					.addContainerGap(10, Short.MAX_VALUE))
		);

		pack();
		setLocationRelativeTo(getOwner());
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

	public void actionPerformed(ActionEvent e) {
		action(e.getActionCommand());
	}
	
	private void action(String command) {
		if (command.equals(CANCEL_ACTION)) {
			this.dispose();
			this.setVisible(false);
			this.setAlwaysOnTop(false);
		} else if (command.equals(RESET_ACTION)) {
			
		} else if (command.equals(SAVE_ACTION)) {
			Settings.setLauncherChannel(LauncherBuild.getType(this.launcherVersion.getSelectedIndex()));
			Settings.setMemory(Memory.memoryOptions[memoryCombo.getSelectedIndex()].getSettingsId());
			Settings.setDebugMode(this.debugMode.isSelected());
			Proxy proxy = new Proxy();
			proxy.setHost(this.proxyHost.getText());
			proxy.setPort(this.proxyPort.getText());
			proxy.setUser(this.proxyUsername.getText());
			proxy.setPass(this.proxyPassword.getPassword());
			proxy.setup();
			Settings.getYAML().save();
		}
	}
}
