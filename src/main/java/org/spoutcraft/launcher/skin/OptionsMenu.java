/*
 * Created by JFormDesigner on Sun Sep 09 18:09:48 EDT 2012
 */

package org.spoutcraft.launcher.skin;

import java.awt.*;
import java.net.URL;

import javax.swing.*;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
import org.spoutcraft.launcher.entrypoint.SpoutcraftLauncher;

/**
 * @author Cameron McAvoy
 */
public class OptionsMenu extends JDialog {
	public static final URL spoutcraftIcon = SpoutcraftLauncher.class.getResource("/org/spoutcraft/launcher/resources/icon.png");
	public OptionsMenu() {
		initComponents();
		
		setTitle("Launcher Options");
		setIconImage(Toolkit.getDefaultToolkit().getImage(spoutcraftIcon));
		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - Cameron McAvoy
		launcherLabel = new JLabel();
		spoutcraftLabel = new JLabel();
		separator1 = new JSeparator();
		launcherVersion = new JComboBox();
		launcherVersionLabel = new JLabel();
		spoutcraftVersionLabel = new JLabel();
		memoryLabel = new JLabel();
		comboBox1 = new JComboBox();
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
		passwordField1 = new JPasswordField();
		comboBox2 = new JComboBox();
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

		//---- spoutcraftVersionLabel ----
		spoutcraftVersionLabel.setText("Version:");
		spoutcraftVersionLabel.setFont(new Font("Arial", Font.PLAIN, 11));

		//---- memoryLabel ----
		memoryLabel.setText("Memory:");
		memoryLabel.setBackground(Color.white);

		//---- debugLabel ----
		debugLabel.setText("Debug Mode:");
		debugLabel.setBackground(Color.white);

		//---- debugMode ----
		debugMode.setBackground(Color.white);

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
		passwordField1.setFont(new Font("Arial", Font.PLAIN, 11));

		//---- cancelButton ----
		cancelButton.setText("Cancel");
		cancelButton.setFont(new Font("Arial", Font.PLAIN, 12));

		//---- resetButton ----
		resetButton.setText("Reset to Defaults");
		resetButton.setFont(new Font("Arial", Font.PLAIN, 12));

		//---- saveButton ----
		saveButton.setText("Save");
		saveButton.setFont(new Font("Arial", Font.PLAIN, 12));

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
											.add(comboBox1, GroupLayout.PREFERRED_SIZE, 93, GroupLayout.PREFERRED_SIZE))
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
										.add(GroupLayout.TRAILING, passwordField1, GroupLayout.PREFERRED_SIZE, 124, GroupLayout.PREFERRED_SIZE)
										.add(GroupLayout.TRAILING, textField1)
										.add(GroupLayout.TRAILING, proxyPort, GroupLayout.PREFERRED_SIZE, 127, GroupLayout.PREFERRED_SIZE)
										.add(GroupLayout.TRAILING, proxyHost)))
								.add(contentPaneLayout.createSequentialGroup()
									.add(contentPaneLayout.createParallelGroup()
										.add(spoutcraftLabel)
										.add(contentPaneLayout.createSequentialGroup()
											.add(spoutcraftVersionLabel)
											.addPreferredGap(LayoutStyle.RELATED)
											.add(comboBox2, GroupLayout.PREFERRED_SIZE, 96, GroupLayout.PREFERRED_SIZE))
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
										.add(comboBox1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
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
								.add(passwordField1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
						.add(separator2, GroupLayout.PREFERRED_SIZE, 133, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(LayoutStyle.RELATED)
					.add(separator1, GroupLayout.PREFERRED_SIZE, 2, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(LayoutStyle.RELATED)
					.add(spoutcraftLabel)
					.addPreferredGap(LayoutStyle.RELATED)
					.add(contentPaneLayout.createParallelGroup(GroupLayout.BASELINE)
						.add(spoutcraftVersionLabel)
						.add(comboBox2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
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
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - Cameron McAvoy
	private JLabel launcherLabel;
	private JLabel spoutcraftLabel;
	private JSeparator separator1;
	private JComboBox launcherVersion;
	private JLabel launcherVersionLabel;
	private JLabel spoutcraftVersionLabel;
	private JLabel memoryLabel;
	private JComboBox comboBox1;
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
	private JPasswordField passwordField1;
	private JComboBox comboBox2;
	private JSeparator separator3;
	private JButton cancelButton;
	private JButton resetButton;
	private JButton saveButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
