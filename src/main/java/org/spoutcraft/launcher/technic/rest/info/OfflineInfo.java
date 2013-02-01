package org.spoutcraft.launcher.technic.rest.info;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.spoutcraft.launcher.util.ResourceUtils;
import org.spoutcraft.launcher.util.Utils;

public class OfflineInfo {
	private final String name;
	private final String version;
	
	public OfflineInfo(String name, String version) {
		this.name = name;
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public BufferedImage getLogo() throws IOException {
		BufferedImage image;
		File temp = new File(Utils.getAssetsDirectory(), getName() + File.separator + "logo.png");
		if (temp.exists()) {
			image = ImageIO.read(temp);
		} else {
			image = ImageIO.read(ResourceUtils.getResourceAsStream("/org/spoutcraft/launcher/resources/noLogo.png"));
		}
		return image;
	}

	public BufferedImage getIcon() throws IOException {
		BufferedImage image;
		File temp = new File(Utils.getAssetsDirectory(), getName() + File.separator + "icon.png");
		if (temp.exists()) {
			image = ImageIO.read(temp);
		} else {
			image = getLogo();
		}
		return image;
	}

	public BufferedImage getBackground() throws IOException {
		BufferedImage image;
		File temp = new File(Utils.getAssetsDirectory(), getName() + File.separator + "background.jpg");
		if (temp.exists()) {
			image = ImageIO.read(temp);
		} else {
			image = ImageIO.read(ResourceUtils.getResourceAsStream("/org/spoutcraft/launcher/resources/background.jpg"));
		}
		return image;
	}
}
