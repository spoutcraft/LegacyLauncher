package org.spoutcraft.launcher.launcher;

import net.technicpack.launchercore.install.user.User;
import net.technicpack.launchercore.install.user.skins.ISkinMapper;
import net.technicpack.launchercore.util.ResourceUtils;
import net.technicpack.launchercore.util.Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TechnicSkinMapper implements ISkinMapper {
	@Override
	public String getSkinFilename(User user) {
		return Utils.getAssetsDirectory() + File.separator + "skins" + File.separator + user.getDisplayName() + ".png";
	}

	@Override
	public String getFaceFilename(User user) {
		return Utils.getAssetsDirectory() + File.separator + "avatars" + File.separator + user.getDisplayName() + ".png";
	}

	@Override
	public BufferedImage getDefaultSkinImage() {
		return null;
	}

	@Override
	public BufferedImage getDefaultFaceImage() {
		try {
			return ImageIO.read(ResourceUtils.getResourceAsStream("/org/spoutcraft/launcher/resources/face.png"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return null;
	}
}
