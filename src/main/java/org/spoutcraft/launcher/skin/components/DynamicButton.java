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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.spoutcraft.launcher.util.ImageUtils;
import org.spoutcraft.launcher.util.SwingWorker;

public class DynamicButton extends JButton implements MouseListener{
	private static final long serialVersionUID = 1L;
	private final AtomicReference<ResizingWorker> worker = new AtomicReference<ResizingWorker>(null);
	private final BufferedImage icon;
	private final int hoverIncrease;
	public DynamicButton(BufferedImage icon, int hoverIncrease) {
		this.icon = icon;
		this.hoverIncrease = hoverIncrease;
		this.setSize(icon.getWidth(), icon.getHeight());
		this.setBorder(null);
		setIcon(new ImageIcon(icon));
		setRolloverEnabled(true);
		setFocusable(false);
		addMouseListener(this);
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
		setIcon(new ImageIcon(ImageUtils.scaleImage(icon, w, h)));
		super.setSize(w, h);
	}

	@Override
	public void setBounds(int x, int y, int w, int h) {
		setIcon(new ImageIcon(ImageUtils.scaleImage(icon, w, h)));
		super.setBounds(x, y, w, h);
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
		setIcon(new ImageIcon(ImageUtils.scaleImage(icon, width + size, height + size)));
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
	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
		updateSize(hoverIncrease);
	}

	public void mouseExited(MouseEvent e) {
		updateSize(-hoverIncrease);
	}
}

