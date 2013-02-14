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

	public void changeIcon(String name, Icon newIcon) {
		if (!name.equals(pack)) {
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
					label.transparency = Math.max(0F, label.transparency - 0.05F);
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
