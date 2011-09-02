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
package org.spoutcraft.launcher.GUI;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.spoutcraft.launcher.PlatformUtils;
import org.spoutcraft.launcher.SettingsHandler;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.Font;

public class OptionDialog extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2453348055512665749L;
	private final JPanel contentPanel = new JPanel();
	private SettingsHandler settings = new SettingsHandler("defaults/spoutcraft.properties", new File(PlatformUtils.getWorkingDirectory(), "spoutcraft" + File.separator + "spoutcraft.properties"));

	JCheckBox devCheckbox = new JCheckBox("Use latest dev build. Dangerous!");
	
	JCheckBox clipboardCheckbox = new JCheckBox("Allow access to your clipboard");
	
	JCheckBox backupCheckbox = new JCheckBox("Include worlds when doing automated backup");
	
	JCheckBox retryLoginCheckbox = new JCheckBox("Retry after connection timeout");


	/**
	 * Create the dialog.
	 */
	public OptionDialog() {
		setTitle("Spoutcraft Settings");
		
		settings.load();
		
		System.out.println(settings.getLineCount());
		
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
		
		setResizable(false);
		setBounds(100, 100, 300, 200);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(devCheckbox)
						.addComponent(clipboardCheckbox)
						.addComponent(backupCheckbox)
						.addComponent(retryLoginCheckbox))
					.addContainerGap(17, Short.MAX_VALUE))
		);
		Font font = new Font("Arial", Font.PLAIN, 11);
		backupCheckbox.setFont(font);
		clipboardCheckbox.setFont(font);
		devCheckbox.setFont(font);
		retryLoginCheckbox.setFont(font);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addComponent(devCheckbox)
					.addComponent(retryLoginCheckbox)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(clipboardCheckbox)
					.addComponent(backupCheckbox)
					.addContainerGap(316, Short.MAX_VALUE))
		);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setFont(new Font("Arial", Font.PLAIN, 11));
				okButton.setActionCommand("OK");
				okButton.addActionListener(this);
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setFont(new Font("Arial", Font.PLAIN, 11));
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
			this.setVisible(false);
			this.dispose();
		} else if (btnID.equals("Cancel")) {
			this.setVisible(false);
			this.dispose();
		}
		
	}
}
