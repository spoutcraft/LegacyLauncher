package org.spoutcraft.launcher.skin.components;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;

public class TransparentJLabel extends JLabel implements MouseListener{
	private static final long serialVersionUID = 1L;
	private float transparency = 1F;
	private float hoverTransparency = 1F;
	public TransparentJLabel() {
		super();
	}
	
	public TransparentJLabel(String text) {
		super(text);
	}

	/**
	 * Gets the transparency of the label when no mouse is hovering over it
	 * 
	 * @return transparency
	 */
	public float getTransparency() {
		return transparency;
	}

	/**
	 * Sets the transparency of the label when no mouse is hovering over it
	 * 
	 * @param t transparency
	 */
	public void setTransparency(float t) {
		this.transparency = t;
	}

	public float getHoverTransparency() {
		return hoverTransparency;
	}
	
	public void setHoverTransparency(float t) {
		this.hoverTransparency = t;
	}

	@Override
	public void paint(Graphics g) {
		if (getTransparency() != 1F) {
			Graphics2D copy = (Graphics2D) g.create();
			copy.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getTransparency()));
			super.paint(copy);
			copy.dispose();
		} else {
			super.paint(g);
		}
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}
}
