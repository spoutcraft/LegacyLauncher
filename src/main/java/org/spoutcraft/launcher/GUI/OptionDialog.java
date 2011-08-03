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

public class OptionDialog extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2453348055512665749L;
	private final JPanel contentPanel = new JPanel();
	private SettingsHandler settings = new SettingsHandler("defaults/spoutcraft.properties", new File(PlatformUtils.getWorkingDirectory(), "spoutcraft" + File.separator + "spoutcraft.properties"));
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			OptionDialog dialog = new OptionDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	JCheckBox cbxDev = new JCheckBox("Use latest dev build. Remember to only use this if you know what you are doing!");
	
	JCheckBox cbxClip = new JCheckBox("Allow access to your clipboard");


	/**
	 * Create the dialog.
	 */
	public OptionDialog() {
		setTitle("Spoutcraft Settings");
		
		settings.load();
		
		setResizable(false);
		setBounds(100, 100, 457, 121);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		JCheckBox chckbxNewCheckBox = new JCheckBox("Use latest dev build. Remember to only use this if you know what you are doing!");
		
		JCheckBox chckbxAllowAccessTo = new JCheckBox("Allow access to your clipboard");
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(chckbxNewCheckBox)
						.addComponent(chckbxAllowAccessTo))
					.addContainerGap(17, Short.MAX_VALUE))
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addComponent(chckbxNewCheckBox)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chckbxAllowAccessTo)
					.addContainerGap(316, Short.MAX_VALUE))
		);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				okButton.addActionListener(this);
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				cancelButton.addActionListener(this);
				buttonPane.add(cancelButton);
			}
		}
	}

	public void actionPerformed(ActionEvent evt) {
		String btnID = evt.getActionCommand(); 
		if (btnID.equals("Ok")) {
			if (settings.checkProperty("devupdate")) {
				settings.changeProperty("devupdate", cbxDev.isSelected());
			} else {
				settings.put("devupdate", cbxDev.isSelected());
			}
			if (settings.checkProperty("clipboardAccess")) {
				settings.changeProperty("clipboardAccess", cbxClip.isSelected());
			} else {
				settings.put("clipboardAccess", cbxClip.isSelected());
			}
			this.setVisible(false);
			this.dispose();
		} else if (btnID.equals("Cancel")) {
			this.setVisible(false);
			this.dispose();
		}
		
	}
}
