package org.spoutcraft.launcher.technic.skin;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.spoutcraft.launcher.technic.ModpackInfo;
import org.spoutcraft.launcher.util.ImageUtils;

public class PackButton {

	private final BufferedImage image;
	private final ModpackInfo info;
	
	public PackButton(ModpackInfo info) throws IOException {
		this.info = info;
		image = info.getImg();
//		image = ImageIO.read(ResourceUtils.getResourceAsStream("/org/spoutcraft/launcher/resources/packsizeforolloth.png"));
	}

	public JLabel createButton(int x, int y, int width, int height) {
		JLabel button = new JLabel();
		button.setBounds(x, y, width, height);
		button.setIcon(new ImageIcon(ImageUtils.scaleImage(image, width, height)));
		System.out.println("Button created for " + info.getName() + " pack!");
		return button;
	}

	public ModpackInfo getModpackInfo() {
		return info;
	}
}
