package org.spoutcraft.launcher.technic.skin;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class ImageButton extends JButton {
	private static final long serialVersionUID = 1L;

	public ImageButton(ImageIcon image, ImageIcon clicked) {
		this.setIcon(image);
		this.setSelectedIcon(image);
		this.setPressedIcon(clicked);
		this.setBorderPainted(false);
		this.setFocusPainted(false);
		this.setContentAreaFilled(false);
	}
}
