/*
 * This file is part of Spoutcraft.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spoutcraft is licensed under the SpoutDev License Version 1.
 *
 * Spoutcraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spoutcraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spoutcraft.launcher.skin.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.AbstractBorder;

public class LiteTextBox extends JTextField implements FocusListener{
	private static final long serialVersionUID = 1L;
	private final JLabel label;
	public LiteTextBox(JFrame parent, String label) {
		this.label = new JLabel(label);
		addFocusListener(this);
		parent.getContentPane().add(this.label);
		this.setBackground(new Color(220, 220, 220));
		this.setBorder(new LiteBorder(5, getBackground()));
	}

	@Override
	public void setFont(Font font) {
		super.setFont(font);
		if (label != null) {
			label.setFont(font);
		}
	}

	@Override
	public void setBounds(int x, int y, int w, int h) {
		super.setBounds(x, y, w, h);
		label.setBounds(x + 5, y + 3, w - 5, h - 5);
	}

	public void focusGained(FocusEvent e) {
		label.setVisible(false);
	}

	public void focusLost(FocusEvent e) {
		if (getText().length() == 0) {
			label.setVisible(true);
		}
	}

	private static class LiteBorder extends AbstractBorder {
		private static final long serialVersionUID = 1L;
		private final int thickness;
		private final Color color;
		public LiteBorder(int thick, Color color) {
			this.thickness = thick;
			this.color = color;
		}

		@Override
		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.setColor(color);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.drawRect(x, y, width, height);
		}

		@Override
		public Insets getBorderInsets(Component c) {
			return new Insets(thickness, thickness, thickness, thickness);
		}

		@Override
		public Insets getBorderInsets(Component c, Insets insets) {
			insets.left = insets.top = insets.right = insets.bottom = thickness;
			return insets;
		}
	}
}
