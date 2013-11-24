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

package org.spoutcraft.launcher.skin;

import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
import org.spoutcraft.launcher.Launcher;
import net.technicpack.launchercore.util.Settings;
import org.spoutcraft.launcher.entrypoint.SpoutcraftLauncher;
import org.spoutcraft.launcher.util.DesktopUtils;
import org.spoutcraft.launcher.util.PasteBinAPI;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Container;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.UnsupportedEncodingException;
import java.net.URL;

public class ErrorDialog extends JDialog implements ActionListener {
	private static final URL technicIcon = ErrorDialog.class.getResource("/org/spoutcraft/launcher/resources/icon.png");
	private static final String CLOSE_ACTION = "close";
	private static final String REPORT_ACTION = "report";
	private static final String PASTEBIN_URL = "http://pastebin.com";
	private static final long serialVersionUID = 1L;
	private final Throwable cause;
	private final String selected;
	private JLabel titleLabel;
	private JLabel exceptionLabel;
	private JScrollPane scrollPane1;
	private JTextArea errorArea;
	private JButton reportButton;
	private JButton closeButton;

	public ErrorDialog(Frame owner, Throwable t) {
		super(owner);
		this.cause = t;
		initComponents();
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(technicIcon));
		populateException(this.cause);
		reportButton.addActionListener(this);
		reportButton.setActionCommand(REPORT_ACTION);
		closeButton.addActionListener(this);
		closeButton.setActionCommand(CLOSE_ACTION);
		String getSelected = null;
		try {
			getSelected = Launcher.getFrame().getSelector().getSelectedPack().getInfo().getDisplayName();
		} catch (Exception e) {
			e.printStackTrace();
			// This is probably a null pointer if it ever gets here
			// but I want to be sure the error window still launches
			// I'll just have less information to work with.
		}
		selected = getSelected;
	}

	private void populateException(Throwable e) {
		StringBuilder builder = new StringBuilder();
		builder.append("Stack Trace:").append("\n");
		builder.append("    Exception: ").append(e.getClass().getSimpleName()).append("\n");
		builder.append("    Message: ").append(e.getMessage()).append("\n");
		logTrace(builder, e);
		errorArea.setText(builder.toString());
	}

	private void logTrace(StringBuilder builder, Throwable e) {
		Throwable parent = e;
		String indent = "    ";
		while (parent != null) {
			if (parent == e) {
				builder.append(indent).append("Trace:").append("\n");
			} else {
				builder.append(indent).append("Caused By: (").append(parent.getClass().getSimpleName()).append(")").append("\n");
				builder.append(indent).append("    ").append("[").append(parent.getMessage()).append("]").append("\n");
			}
			for (StackTraceElement ele : e.getStackTrace()) {
				builder.append(indent).append("    ").append(ele.toString()).append("\n");
			}
			indent += "    ";
			parent = parent.getCause();
		}
	}

	private void initComponents() {
		titleLabel = new JLabel();
		exceptionLabel = new JLabel();
		scrollPane1 = new JScrollPane();
		errorArea = new JTextArea();
		reportButton = new JButton();
		closeButton = new JButton();

		//======== this ========
		setAlwaysOnTop(true);
		setModal(true);
		setTitle("Unexpected Error!");
		Container contentPane = getContentPane();

		//---- titleLabel ----
		titleLabel.setText("An unexpected error has occured - Please report to https://github.com/TechnicPack/TechnicLauncher/issues");
		titleLabel.setFont(new Font("Arial", Font.PLAIN, 11));

		//---- exceptionLabel ----
		exceptionLabel.setText("Error:");
		exceptionLabel.setFont(new Font("Arial", Font.PLAIN, 11));

		//======== scrollPane1 ========
		{
			scrollPane1.setViewportView(errorArea);
		}

		//---- reportButton ----
		reportButton.setText("Generate Report");
		reportButton.setFont(new Font("Arial", Font.PLAIN, 11));

		//---- closeButton ----
		closeButton.setText("Close Application");
		closeButton.setFont(new Font("Arial", Font.PLAIN, 11));

		GroupLayout contentPaneLayout = new GroupLayout(contentPane);
		contentPane.setLayout(contentPaneLayout);
		contentPaneLayout.setHorizontalGroup(
				contentPaneLayout.createParallelGroup()
						.add(contentPaneLayout.createSequentialGroup()
								.addContainerGap()
								.add(contentPaneLayout.createParallelGroup()
										.add(contentPaneLayout.createSequentialGroup()
												.add(reportButton, GroupLayout.PREFERRED_SIZE, 142, GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(LayoutStyle.RELATED, 458, Short.MAX_VALUE)
												.add(closeButton, GroupLayout.PREFERRED_SIZE, 139, GroupLayout.PREFERRED_SIZE))
										.add(titleLabel, GroupLayout.DEFAULT_SIZE, 739, Short.MAX_VALUE)
										.add(GroupLayout.TRAILING, scrollPane1, GroupLayout.DEFAULT_SIZE, 739, Short.MAX_VALUE)
										.add(contentPaneLayout.createSequentialGroup()
												.add(exceptionLabel)
												.add(0, 0, Short.MAX_VALUE)))
								.addContainerGap())
		);
		contentPaneLayout.setVerticalGroup(
				contentPaneLayout.createParallelGroup()
						.add(contentPaneLayout.createSequentialGroup()
								.addContainerGap()
								.add(titleLabel)
								.addPreferredGap(LayoutStyle.RELATED)
								.add(exceptionLabel)
								.addPreferredGap(LayoutStyle.RELATED)
								.add(scrollPane1, GroupLayout.PREFERRED_SIZE, 270, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.UNRELATED)
								.add(contentPaneLayout.createParallelGroup(GroupLayout.BASELINE)
										.add(closeButton)
										.add(reportButton))
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		pack();
		setLocationRelativeTo(getOwner());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		action(e.getActionCommand());
	}

	private void action(String command) {
		if (command.equals(CLOSE_ACTION)) {
			closeForm();
		} else if (command.equals(REPORT_ACTION)) {
			PasteBinAPI pastebin = new PasteBinAPI("963f01dd506cb3f607a487bc34b60d16");
			String response = "";
			try {
				response = pastebin.makePaste(generateExceptionReport(), "ser_" + System.currentTimeMillis(), "text");

				if (response == null ) {
					JOptionPane.showMessageDialog(this, "Check your internet and system settings.","PasteBin Inaccessible", JOptionPane.ERROR_MESSAGE);
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if (response.startsWith(PASTEBIN_URL)) {
				try {
					DesktopUtils.browse((new URL(response)).toURI());
				} catch (Exception e) {
					System.err.println("Unable to generate error report. Response: " + response);
					e.printStackTrace();
				}
			} else {
				System.err.println("Unable to generate error report. Response: " + response);
			}
			closeForm();
		}
	}

	private String generateExceptionReport() {
		StringBuilder builder = new StringBuilder("Technic Launcher Error Report:\n");
		builder.append("( Please submit this report to https://github.com/TechnicPack/TechnicLauncher/issues )\n");
		builder.append("    Launcher Build: ").append(SpoutcraftLauncher.getLauncherBuild()).append("\n").append("\n");
		builder.append("    Selected Pack: ").append(selected).append("\n");
		builder.append("Stack Trace:").append("\n");
		builder.append("    Exception: ").append(cause.getClass().getSimpleName()).append("\n");
		builder.append("    Message: ").append(cause.getMessage()).append("\n");
		logTrace(builder, cause);
		builder.append("\n");
		builder.append("System Information:\n");
		builder.append("    Operating System: ").append(System.getProperty("os.name")).append("\n");
		builder.append("    Operating System Version: ").append(System.getProperty("os.version")).append("\n");
		builder.append("    Operating System Architecture: ").append(System.getProperty("os.arch")).append("\n");
		builder.append("    Java version: ").append(System.getProperty("java.version")).append(" ").append(System.getProperty("sun.arch.data.model", "32")).append(" bit").append("\n");
		builder.append("    Total Memory: ").append(Runtime.getRuntime().totalMemory() / 1024L / 1024L).append(" MB\n");
		builder.append("    Max Memory: ").append(Runtime.getRuntime().maxMemory() / 1024L / 1024L).append(" MB\n");
		builder.append("    Memory Free: ").append(Runtime.getRuntime().freeMemory() / 1024L / 1024L).append(" MB\n");
		builder.append("    CPU Cores: ").append(Runtime.getRuntime().availableProcessors()).append("\n");
		return builder.toString();
	}

	private void closeForm() {
		this.dispose();
		System.exit(0);
	}
}
