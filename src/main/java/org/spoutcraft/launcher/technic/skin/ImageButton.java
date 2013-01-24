package org.spoutcraft.launcher.technic.skin;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class ImageButton extends JButton {
	private static final long serialVersionUID = 1L;

	public ImageButton(ImageIcon image) {
		this(image, image);
	}

	public ImageButton(ImageIcon image, ImageIcon rollover) {
		this();
		this.setIcon(image);
		this.setRolloverIcon(rollover);
		
	}

	public ImageButton() {
		this.setBorderPainted(false);
		this.setFocusPainted(false);
		this.setContentAreaFilled(false);
	}
}
