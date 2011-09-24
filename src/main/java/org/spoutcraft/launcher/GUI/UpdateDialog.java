package org.spoutcraft.launcher.GUI;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import java.awt.Font;

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
		setBounds(100, 100, 450, 136);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		setIconImage(Toolkit.getDefaultToolkit().getImage(LoginForm.class.getResource("/org/spoutcraft/launcher/favicon.png")));

		{
			label.setFont(new Font("Arial", Font.PLAIN, 18));
			contentPanel.add(label);
		}
		{
			JLabel lblThereIsA = new JLabel("Would you like to update?");
			lblThereIsA.setFont(new Font("Arial", Font.PLAIN, 18));
			contentPanel.add(lblThereIsA);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Yes");
				okButton.addActionListener(this);
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("No");
				buttonPane.add(cancelButton);
				cancelButton.addActionListener(this);
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Yes")) {
			lf.updateThread();
		}
		this.setVisible(false);
	}

}
