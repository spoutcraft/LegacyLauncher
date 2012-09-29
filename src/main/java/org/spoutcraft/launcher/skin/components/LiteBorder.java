package org.spoutcraft.launcher.skin.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.border.AbstractBorder;

class LiteBorder extends AbstractBorder {
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