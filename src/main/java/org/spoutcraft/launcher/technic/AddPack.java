package org.spoutcraft.launcher.technic;

import java.awt.Image;
import java.io.IOException;

import javax.swing.ImageIcon;

import org.spoutcraft.launcher.skin.MetroLoginFrame;

public class AddPack extends InstalledPack {
	private final static Image icon = MetroLoginFrame.getIcon("icon.png", 32, 32).getImage();
	private final static Image image = MetroLoginFrame.getIcon("addNewPack.png", 180, 110).getImage();
	private final static ImageIcon background = new ImageIcon(MetroLoginFrame.getIcon("background.jpg", 880, 520).getImage().getScaledInstance(880, 520, Image.SCALE_SMOOTH));

	public AddPack() throws IOException {
		super(null, icon, image, background);
	}

	@Override
	public String getDisplayName() {
		return "Add New Pack";
	}
}
