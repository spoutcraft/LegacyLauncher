/*
 * This file is part of Spoutcraft.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * Spoutcraft is licensed under the Spout License Version 1.
 *
 * Spoutcraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spoutcraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spoutcraft.launcher.util;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.DefaultButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import static org.spoutcraft.launcher.util.ResourceUtils.*;

public class ImageUtils {
	private static int SKIN_WIDTH = 64;
	private static int SKIN_HEIGHT = 32;

	public static BufferedImage scaleImage(BufferedImage img, int width, int height) {
		int imgWidth = img.getWidth();
		int imgHeight = img.getHeight();
		if (imgWidth * height < imgHeight * width) {
			width = imgWidth * height / imgHeight;
		} else {
			height = imgHeight * width / imgWidth;
		}
		BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = newImage.createGraphics();
		try {
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.drawImage(img, 0, 0, width, height, null);
		} finally {
			g.dispose();
		}
		return newImage;
	}

	public static void drawCharacter(JPanel contentPane, ActionListener listener, String url, int x, int y, List<JButton> buttons) {
		BufferedImage image = getSkinImage(url);
		int type = BufferedImage.TYPE_INT_ARGB;

		buttons.add(drawCropped(contentPane, listener, image, type, 8, 8, 16, 16, x, y, 7)); // HEAD

		buttons.add(drawCropped(contentPane, listener, image, type, 20, 20, 28, 32, x, y + 56, 7)); // BODY

		buttons.add(drawCropped(contentPane, listener, image, type, 44, 20, 48, 32, x - 28, y + 56, 7)); // ARMS
		buttons.add(drawCropped(contentPane, listener, image, type, 44, 20, 48, 32, x + 56, y + 56, 7, true));

		buttons.add(drawCropped(contentPane, listener, image, type, 4, 20, 8, 32, x, y + 140, 7)); // LEGS
		buttons.add(drawCropped(contentPane, listener, image, type, 4, 20, 8, 32, x + 28, y + 140, 7, true));

		List<JButton> modelList = new ArrayList<JButton>(buttons);

		buttons.add(getShadow(contentPane, modelList, x, y, image));
	}

	public static JButton drawCropped(JPanel contentPane, ActionListener listener, BufferedImage img, int type, int sx1, int sy1, int sx2, int sy2, int x, int y, int scale) {
		return drawCropped(contentPane, listener, img, type, sx1, sy1, sx2, sy2, x, y, scale, false);
	}

	public static JButton drawCropped(JPanel contentPane, ActionListener listener, BufferedImage img, int type, int sx1, int sy1, int sx2, int sy2, int x, int y, int scale, boolean reflect) {
		BufferedImage resizedImage = new BufferedImage((sx2 - sx1) * scale, (sy2 - sy1) * scale, type);
		Graphics2D g = resizedImage.createGraphics();
		int asx2 = sx2, asx1 = sx1;
		if (reflect) {
			asx2 = sx1;
			asx1 = sx2;
		}
		g.drawImage(img, 0, 0, (sx2 - sx1) * scale, (sy2 - sy1) * scale, asx1, sy1, asx2, sy2, null);
		g.dispose();

		JButton tmp = new JButton(new ImageIcon(resizedImage));
		tmp.setRolloverEnabled(true);
		tmp.setRolloverIcon(tmp.getIcon());
		tmp.setSelectedIcon(tmp.getIcon());
		tmp.setDisabledIcon(tmp.getIcon());
		tmp.setPressedIcon(tmp.getIcon());
		tmp.setFocusable(false);
		tmp.setContentAreaFilled(false);
		tmp.setBorderPainted(false);

		tmp.setBounds(x, y, (sx2 - sx1) * scale, (sy2 - sy1) * scale);
		if (listener != null) {
			tmp.addActionListener(listener);
		}
		contentPane.add(tmp);
		return tmp;
	}

	private static BufferedImage getSkinImage(String url) {
		BufferedImage image = null;
		try {
			URLConnection conn = (new URL(url)).openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(false);
			System.setProperty("http.agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.162 Safari/535.19");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.162 Safari/535.19");
			HttpURLConnection.setFollowRedirects(true);
			conn.setUseCaches(false);
			((HttpURLConnection)conn).setInstanceFollowRedirects(true);
			int response = ((HttpURLConnection)conn).getResponseCode();
			if (response == HttpURLConnection.HTTP_OK) {
				image = ImageIO.read(conn.getInputStream());
				if (image.getWidth() != SKIN_WIDTH || image.getHeight() != SKIN_HEIGHT) {
					BufferedImage resized = new BufferedImage(SKIN_WIDTH, SKIN_HEIGHT, image.getType());
					Graphics2D g = resized.createGraphics();
					g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
					g.drawImage(image, 0, 0, SKIN_WIDTH, SKIN_HEIGHT, 0, 0, image.getWidth(), image.getHeight(), null);
					g.dispose();
					image = resized;
				}
			}
			if (image != null) {
				return image;
			}
			return ImageIO.read(getResourceAsStream("/org/spoutcraft/launcher/resources/char.png"));
		} catch (Exception e) {
			try {
				return ImageIO.read(getResourceAsStream("/org/spoutcraft/launcher/resources/char.png"));
			} catch (IOException e1) {
				throw new RuntimeException("Error loading cached image resource", e1);
			}
		}
	}

	private static final int AVG_COLOR = 0xFF / 2;
	private static JButton getShadow(JPanel contentPane, List<JButton> buttons, int x, int y, BufferedImage skinImage) {
		long red = 0, green = 0, blue = 0;
		int count = 0;
		for (int dx = 0; dx < skinImage.getWidth(); dx++) {
			for (int dy = 0; dy < skinImage.getHeight(); dy++) {
				int rgb = skinImage.getRGB(dx, dy);
				if (rgb != 0) {
					red += (rgb & 0x00FF0000) >> 16;
					green += (rgb & 0x0000FF00) >> 8;
					blue += rgb & 0x000000FF;
					count++;
				}
			}
		}
		long sum = ((red / count) + (green / count) + (blue / count)) / 3;
		try {
			ImageIcon whiteGlow, blackGlow;
			blackGlow = new ImageIcon(ImageIO.read(getResourceAsStream("/org/spoutcraft/launcher/resources/char_normal.png")));
			whiteGlow = new ImageIcon(ImageIO.read(getResourceAsStream("/org/spoutcraft/launcher/resources/char_hover.png")));

			ImageIcon main, hover;
			if (sum < AVG_COLOR) {
				main = blackGlow;
				hover = whiteGlow;
			} else {
				main = whiteGlow;
				hover = blackGlow;
			}

			JButton tmp = new JButton(main);
			tmp.setRolloverEnabled(true);
			tmp.setModel(new RolloverModel(buttons));
			tmp.setRolloverIcon(hover);
			tmp.setSelectedIcon(tmp.getIcon());
			tmp.setDisabledIcon(tmp.getIcon());
			tmp.setPressedIcon(tmp.getIcon());
			tmp.setFocusable(false);
			tmp.setContentAreaFilled(false);
			tmp.setBorderPainted(false);
			tmp.setBounds(x - 41, y - 18, whiteGlow.getIconWidth(), whiteGlow.getIconHeight());
			contentPane.add(tmp);

			return tmp;
		} catch (Exception e) {
			throw new RuntimeException("Error loading cached image resource", e);
		}
	}
}

class RolloverModel extends DefaultButtonModel {
	private static final long serialVersionUID = 1L;
	private final List<JButton> buttons;
	private boolean previous = false;
	public RolloverModel(List<JButton> buttons) {
		this.buttons = buttons;
	}

	@Override
	public boolean isRollover() {
		boolean current = isRolloverImpl();
		if (current != previous) {
			previous = current;
			fireStateChanged();
		}
		return current;
	}

	public boolean isRolloverImpl() {
		for (JButton button : buttons) {
			if (button.getModel().isRollover()) {
				return true;
			}
		}
		return false;
	}
}