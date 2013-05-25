/*
 * This file is part of Spoutcraft Launcher.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spoutcraft Launcher is licensed under the Spout License Version 1.
 *
 * Spoutcraft Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spoutcraft Launcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license,
 * including the MIT license.
 */
package org.spoutcraft.launcher.skin.components;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.spoutcraft.launcher.util.ImageUtils;
import org.spoutcraft.launcher.util.SwingWorker;

public class DynamicButton extends JButton implements MouseListener {
	private static final long serialVersionUID = 1L;
	private final AtomicReference<ResizingWorker> worker = new AtomicReference<ResizingWorker>(null);
	private final FutureImage icon;
	private final int hoverIncrease;
	private final DynamicLabel underLabel;
	private final TransparentButton remove;
	private final String account, userName;
	public DynamicButton(JFrame parent, FutureImage icon, int hoverIncrease, String account, String userName) {
		this.icon = icon;
		this.hoverIncrease = hoverIncrease;
		this.account = account;
		this.userName = userName;
		underLabel = new DynamicLabel(userName);
		remove = new TransparentButton();
		this.setSize(icon.getWidth(), icon.getHeight());
		this.setBorder(null);
		setIcon(new ImageIcon(icon));
		setRolloverEnabled(true);
		setFocusable(false);
		addMouseListener(this);
		underLabel.setForeground(Color.WHITE);
		parent.getContentPane().add(underLabel);
		parent.getContentPane().add(remove);

		remove.setTransparency(0F);
	}

	public JButton getRemoveIcon() {
		return remove;
	}

	public String getUsername() {
		return userName;
	}

	public String getAccount() {
		return account;
	}

	@Override
	public void setText(String text) {
		if (underLabel != null) {
			this.underLabel.setText(text);
		}
	}

	@Override
	public void setFont(Font font) {
		if (underLabel != null) {
			this.underLabel.setFont(font);
		}
	}

	@Override
	public void setIcon(Icon icon) {
		super.setIcon(icon);
		setRolloverIcon(getIcon());
		setSelectedIcon(getIcon());
		setDisabledIcon(getIcon());
		setPressedIcon(getIcon());
	}

	@Override
	public void setSize(int w, int h) {
		setBounds(getX(), getY(), w, h);
	}

	@Override
	public void setBounds(int x, int y, int w, int h) {
		setIcon(new ImageIcon(ImageUtils.scaleImage(icon.getRaw(), w, h)));
		super.setBounds(x, y, w, h);
		remove.setBounds(x + w + 2, y, 16, 16);

		// Allow the label to overflow the button width
		int sw = underLabel.getFontMetrics(underLabel.getFont()).stringWidth(underLabel.getText());
		if (sw > w) {
			x -= ((sw - w) / 2);
			w = sw;

		}
		underLabel.setBounds(x + (w - sw) / 2, y + h, w, 20);


	}

	private void updateSize(int size) {
		if (worker.get() != null) {
			worker.get().cancel(true);
			size += worker.get().size;
			worker.set(null);
		}
		worker.set(new ResizingWorker(size));
		worker.get().execute();
	}

	private void updateSizeImpl(int size) {
		int width = getIcon().getIconWidth();
		int height = getIcon().getIconHeight();
		setBounds(getX() - size / 2, getY() - size / 2, width + size, height + size);
		setIcon(new ImageIcon(ImageUtils.scaleImage(icon.getRaw(), width + size, height + size)));
		if (size > 0) {
			remove.setTransparency(0.4F);
			remove.setHoverTransparency(1F);
		} else {
			remove.setTransparency(0.0F);
			remove.setHoverTransparency(1.0F);
		}
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
		updateSize(hoverIncrease);
		underLabel.setVisible(true);
	}

	public void mouseExited(MouseEvent e) {
		updateSize(-hoverIncrease);
		underLabel.setVisible(false);
	}

	private static class DynamicLabel extends JLabel {
		private static final long serialVersionUID = 1L;
		protected final AtomicReference<TransparencyWorker> worker = new AtomicReference<TransparencyWorker>(null);
		protected volatile float transparency = 0F;

		public DynamicLabel(String text) {
			super(text);
			super.setVisible(true);
		}

		@Override
		public void setVisible(boolean visible) {
			if (visible && transparency < 1) {
				if (worker.get() != null) {
					worker.get().cancel(true);
				}
				worker.set(new TransparencyWorker(this, true));
				worker.get().execute();
			} else if (!visible && transparency > 0) {
				if (worker.get() != null) {
					worker.get().cancel(true);
				}
				worker.set(new TransparencyWorker(this, false));
				worker.get().execute();
			}
		}

		@Override
		public void paint(Graphics g) {
			Graphics2D copy = (Graphics2D) g.create();
			copy.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));
			super.paint(copy);
			copy.dispose();
		}
	}

	private static class TransparencyWorker extends SwingWorker<Object, Object> {
		private final DynamicLabel label;
		private final boolean increase;
		TransparencyWorker(DynamicLabel label, boolean increase) {
			this.label = label;
			this.increase = increase;
		}

		protected Object doInBackground() throws Exception {
			try {
				Thread.sleep(5);
			} catch (InterruptedException ignore) { }
			return null;
		}

		@Override
		protected void done() {
			if (increase) {
				if (label.transparency < 1) {
					label.transparency = Math.min(1F, label.transparency + 0.05F);
					label.repaint();
					if (label.worker.compareAndSet(this, new TransparencyWorker(label, increase))) {
						label.worker.get().execute();
					}
				}
			} else {
				 if (label.transparency > 0) {
					 label.transparency = Math.max(0F, label.transparency - 0.05F);
					 label.repaint();
					 if (label.worker.compareAndSet(this, new TransparencyWorker(label, increase))) {
						 label.worker.get().execute();
					 }
				 }
			}
		}
	}

	private class ResizingWorker extends SwingWorker<Object, Object> {
		private static final int HOVER_SIZE_INCREMENT = 4;
		private final int size;
		ResizingWorker(int size) {
			this.size = size;
		}

		protected Object doInBackground() throws Exception {
			try {
				Thread.sleep(5);
			} catch (InterruptedException ignore) { }
			return null;
		}

		@Override
		protected void done() {
			updateSizeImpl(size > 0 ? HOVER_SIZE_INCREMENT : -HOVER_SIZE_INCREMENT);
			if (size > HOVER_SIZE_INCREMENT) {
				if (worker.compareAndSet(this, new ResizingWorker(size - HOVER_SIZE_INCREMENT))) {
					worker.get().execute();
				}
			} else if (size < -HOVER_SIZE_INCREMENT) {
				if (worker.compareAndSet(this, new ResizingWorker(size + HOVER_SIZE_INCREMENT))) {
					worker.get().execute();
				}
			}
		}
	}
}

