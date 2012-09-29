package org.spoutcraft.launcher.skin.components;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;

/**
 * Adds transparency to a swing component
 */
class TransparentComponent implements MouseListener{
	private final JComponent parent;
	private float transparency = 1F;
	private float hoverTransparency = 1F;
	private boolean hovering = false;
	public TransparentComponent(JComponent component) {
		this.parent = component;
		parent.addMouseListener(this);
	}

	/**
	 * Gets the transparency of the label when no mouse is hovering over it
	 * 
	 * Values are between 0 and 1
	 * 
	 * @return transparency
	 */
	public float getTransparency() {
		return transparency;
	}

	/**
	 * Sets the transparency of the label when no mouse is hovering over it
	 * 
	 * Values should be between 0 and 1
	 * 
	 * @param t transparency
	 */
	public void setTransparency(float t) {
		if (t > 1F || t < 0F) {
			throw new IllegalArgumentException("Value out of range");
		}
		this.transparency = t;
	}

	/**
	 * Gets the transparency of the label when a mouse is hovering over it
	 * 
	 * Values are between 0 and 1
	 * 
	 * @return transparency
	 */
	public float getHoverTransparency() {
		return hoverTransparency;
	}

	/**
	 * Sets the transparency of the label when a mouse is hovering over it
	 * 
	 * Values should be between 0 and 1
	 * 
	 * @param t transparency
	 */
	public void setHoverTransparency(float t) {
		if (t > 1F || t < 0F) {
			throw new IllegalArgumentException("Value out of range");
		}
		this.hoverTransparency = t;
	}

	public Graphics setup(Graphics g) {
		float t;
		if (hovering) {
			t = getHoverTransparency();
		} else {
			t = getTransparency();
		}
		Graphics2D copy = (Graphics2D) g.create();
		copy.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, t));
		return copy;
	}

	public Graphics cleanup(Graphics g) {
		g.dispose();
		return g;
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
		if (e.getComponent() == parent) {
			hovering = true;
			parent.repaint();
		}
	}

	public void mouseExited(MouseEvent e) {
		if (e.getComponent() == parent) {
			hovering = false;
			parent.repaint();
		}
	}
}
