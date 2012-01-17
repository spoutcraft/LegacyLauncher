/*
 * This file is part of Spoutcraft Launcher (http://www.spout.org/).
 *
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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spoutcraft.launcher.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class UpdateDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = -4617588853047124397L;
	private final JPanel contentPanel = new JPanel();
	private JLabel label = new JLabel("There is a new update for %TO_UPDATE%.");
	private LoginForm lf;

	public void setToUpdate(String str) {
		label.setText(label.getText().replace("%TO_UPDATE%", str));
	}

	public UpdateDialog(LoginForm lf) {
		this.lf = lf;
		setBounds((Toolkit.getDefaultToolkit().getScreenSize().width - 450) / 2, (Toolkit.getDefaultToolkit().getScreenSize().height - 136) / 2, 450, 136);
		this.toFront();
		this.setAlwaysOnTop(true);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		//setIconImage(Toolkit.getDefaultToolkit().getImage(LoginForm.class.getResource("/resources/favicon.png")));

		label.setFont(new Font("Arial", Font.PLAIN, 18));
		contentPanel.add(label);
		JLabel lblThereIsA = new JLabel("Would you like to update?");
		lblThereIsA.setFont(new Font("Arial", Font.PLAIN, 18));
		contentPanel.add(lblThereIsA);
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		JButton okButton = new JButton("Yes");
		okButton.addActionListener(this);
		okButton.setActionCommand("Yes");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
		JButton cancelButton = new JButton("No");
		buttonPane.add(cancelButton);
		cancelButton.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Yes")) {
			lf.updateThread();
		} else {
			lf.runGame();
		}
		this.setVisible(false);
	}
}
