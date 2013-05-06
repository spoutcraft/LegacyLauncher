/*
 * This file is part of Technic Launcher.
 *
 * Copyright (c) 2013-2013, Technic <http://www.technicpack.net/>
 * Technic Launcher is licensed under the Spout License Version 1.
 *
 * Technic Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Technic Launcher is distributed in the hope that it will be useful,
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
package org.spoutcraft.launcher.skin.components;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.Icon;
import javax.swing.JLabel;

import org.spoutcraft.launcher.util.SwingWorker;

public class AnimatedBackground extends JLabel {
	private static final long serialVersionUID = 1L;
	protected final AtomicReference<TransparencyWorker> worker = new AtomicReference<TransparencyWorker>(null);
	protected volatile float transparency = 0F;
	private String pack = null;
	private Icon newIcon = null;
	private BackgroundImage background;

	public AnimatedBackground(BackgroundImage background) {
		super();
		super.setVisible(true);
		this.background = background;
	}

	public void changeIcon(String name, Icon newIcon, boolean force) {
		if (!name.equals(pack) || force) {
			this.newIcon = newIcon;
			if (worker.get() != null) {
				worker.get().cancel(true);
			}
			setVisible(false);
			pack = name;
			background.setIcon(getIcon());
		}
	}

	public Icon getNewIcon() {
		return newIcon;
	}

	public BackgroundImage getBackgroundImg() {
		return background;
	}

	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			if (worker.get() != null) {
				worker.get().cancel(true);
			}
			worker.set(new TransparencyWorker(this, true));
			worker.get().execute();
		} else if (!visible) {
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

	private static class TransparencyWorker extends SwingWorker<Object, Object> {
		private final AnimatedBackground label;
		private final boolean increase;
		TransparencyWorker(AnimatedBackground label, boolean increase) {
			this.label = label;
			this.increase = increase;
		}

		@Override
		protected Object doInBackground() throws Exception {
			try {
				Thread.sleep(10);
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
					label.transparency = Math.max(0F, label.transparency - 0.10F);
					label.repaint();
					if (label.worker.compareAndSet(this, new TransparencyWorker(label, increase))) {
						label.worker.get().execute();
					}
				} else {
					label.setIcon(label.getNewIcon());
					label.setVisible(true);
				}
			}
		}
	}
}
