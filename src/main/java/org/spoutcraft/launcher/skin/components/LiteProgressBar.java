package org.spoutcraft.launcher.skin.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JProgressBar;

public class LiteProgressBar extends JProgressBar implements Transparent{
	private static final long serialVersionUID = 1L;
	private final TransparentComponent transparency = new TransparentComponent(this, false);
	public LiteProgressBar() {
		setFocusable(false);
		setOpaque(false);
	}
	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) transparency.setup(g);
		
		// Draw bar
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		
		//Draw progress
		g2d.setColor(Color.BLUE);
		int x = (int) (getWidth() * getPercentComplete());
		g2d.fillRect(0, 0, x, getHeight());

		transparency.cleanup(g2d);
		g2d = (Graphics2D) g;
		
		if (this.isStringPainted()) {
			g2d.setFont(getFont());
			g2d.setColor(Color.BLACK);
			g2d.drawString(this.getString(), (getWidth() - g2d.getFontMetrics().stringWidth(getString())) / 2, getHeight() - 7);
		}
		
		transparency.cleanup(g2d);
	}

	public float getTransparency() {
		return transparency.getTransparency();
	}

	public void setTransparency(float t) {
		transparency.setTransparency(t);
	}

	public float getHoverTransparency() {
		return transparency.getHoverTransparency();
	}

	public void setHoverTransparency(float t) {
		transparency.setHoverTransparency(t);
	}
}
