package org.spoutcraft.launcher;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JWindow;

public class SplashScreen extends JWindow implements Runnable {
	private static final long serialVersionUID = 0L;
	private static Image bg = Toolkit.getDefaultToolkit().getImage(SplashScreen.class.getResource("/org/spoutcraft/launcher/resources/splash.png"));

	ImageIcon icon = new ImageIcon(bg);

	@Override
	public void paint(Graphics g) {
		g.drawImage(bg, 0, 0, this);
	}

	public void run() {
		showSplash();
	}

	public void showSplash() {
		try {
			setSize(this.icon.getIconWidth(), this.icon.getIconHeight());
			setLocationRelativeTo(null);
			setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}