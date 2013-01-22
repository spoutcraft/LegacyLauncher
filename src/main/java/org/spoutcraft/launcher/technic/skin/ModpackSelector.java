package org.spoutcraft.launcher.technic.skin;

import java.awt.Container;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.spoutcraft.launcher.skin.components.DynamicButton;
import org.spoutcraft.launcher.technic.ModpackInfo;
import org.spoutcraft.launcher.technic.TechnicRestAPI;
import org.spoutcraft.launcher.util.ImageUtils;
import org.spoutcraft.launcher.util.ResourceUtils;

public class ModpackSelector extends JComponent {
	private static final long serialVersionUID = 1L;

	private final JFrame frame;
	private List<PackButton> buttons = new ArrayList<PackButton>();

	private final int bigWidth = 180;
	private final int bigHeight = 110;
	private final float smallScale = 0.7F;
	private final int spacing = 15;
	private final int smallWidth = (int) (bigWidth * smallScale);
	private final int smallHeight = (int) (bigHeight * smallScale);

	public ModpackSelector(JFrame frame) {
		this.frame = frame;
	}


	public void setupModpackButtons() throws IOException {
		ModpackInfo[] modpacks = TechnicRestAPI.getModpacks();
		for (int i = 0; i < modpacks.length; i++) {
			buttons.add(new PackButton(modpacks[i]));
		}
		selectPack(0);
	}

	public void selectPack(int index) {
		// Set the big button in the middle
		int bigX = (getWidth() / 2) - (bigWidth / 2);
		int bigY = (getHeight() / 2) - (bigHeight / 2);
		this.add(buttons.get(index).createButton(bigX, bigY, bigWidth, bigHeight));

		int smallY = getHeight() / 2 - smallHeight / 2;

		// Start the iterator at the selected pack
		ListIterator<PackButton> iterator = buttons.listIterator(index);
		// Add the first 3 buttons to the right
		for (int i = 0; i < 3; i++) {
			// If you run out of packs, start the iterator back at 0
			if (!iterator.hasNext()) {
				iterator = buttons.listIterator(0);
			}
			PackButton button = iterator.next();
			int smallX = bigX + bigWidth + spacing + (i * (smallWidth + spacing));
			this.add(button.createButton(smallX, smallY, smallWidth, smallHeight));

		}

		// Start the iterator at the selected pack
		iterator = buttons.listIterator(index);
		// Add the last 3 buttons to the left
		for (int i = 3; i > 0; i--) {
			// If you run out of packs, start the iterator back at the last element
			if (!iterator.hasPrevious()) {
				iterator = buttons.listIterator(buttons.size() - 1);
			}
			PackButton button = iterator.previous();
			int smallX = bigX - spacing - (i * (smallWidth + spacing));
			this.add(button.createButton(smallX, smallY, smallWidth, smallHeight));
		}
	}
}
