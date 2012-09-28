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
	private boolean hovering = false;
	public TransparentJLabel() {
		super();
		addMouseListener(this);
	}

	public TransparentJLabel(String text) {
		super(text);
		addMouseListener(this);
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

	@Override
	public void paint(Graphics g) {
		float t;
		if (hovering) {
			t = getHoverTransparency();
		} else {
			t = getTransparency();
		}
		Graphics2D copy = (Graphics2D) g.create();
		copy.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, t));
		super.paint(copy);
		copy.dispose();
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
		if (e.getComponent() == this) {
			hovering = true;
			this.repaint();
		}
	}

	public void mouseExited(MouseEvent e) {
		if (e.getComponent() == this) {
			hovering = false;
			this.repaint();
		}
	}
}
