package org.spoutcraft.launcher.skin.components;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.spoutcraft.launcher.skin.ImageCallback;

public class FutureImage extends Image implements ImageCallback{
	private final BufferedImage empty;
	private volatile BufferedImage futureImage = null;
	private JComponent repaintCallback = null;

	/**
	 * Future image and empty image must be the same height and width
	 * 
	 * @param future
	 * @param empty
	 */
	public FutureImage(BufferedImage empty) {
		this.empty = empty;
	}

	public void setRepaintCallback(JComponent comp) {
		this.repaintCallback = comp;
	}

	public void done(BufferedImage done) {
		this.futureImage = done;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (repaintCallback != null) {
					repaintCallback.setBounds(repaintCallback.getX(), repaintCallback.getY(), repaintCallback.getWidth(), repaintCallback.getHeight());
					repaintCallback.repaint();
				}
			}
		});
	}

	public int getWidth() {
		return empty.getWidth();
	}

	public int getHeight() {
		return empty.getWidth();
	}

	public BufferedImage getRaw() {
		if (futureImage == null) {
			return empty;
		}
		return futureImage;
	}

	@Override
	public int getWidth(ImageObserver observer) {
		if (futureImage == null) {
			return empty.getWidth(observer);
		}
		return futureImage.getWidth(observer);
	}

	@Override
	public int getHeight(ImageObserver observer) {
		if (futureImage == null) {
			return empty.getHeight(observer);
		}
		return futureImage.getHeight(observer);
	}

	@Override
	public ImageProducer getSource() {
		if (futureImage == null) {
			return empty.getSource();
		}
		return futureImage.getSource();
	}

	@Override
	public Graphics getGraphics() {
		if (futureImage == null) {
			return empty.getGraphics();
		}
		return futureImage.getGraphics();
	}

	@Override
	public Object getProperty(String name, ImageObserver observer) {
		if (futureImage == null) {
			return empty.getProperty(name, observer);
		}
		return futureImage.getProperty(name, observer);
	}

	@Override
	public void flush() {
		if (futureImage != null) {
			futureImage.flush();
		}
		empty.flush();
	}
}
