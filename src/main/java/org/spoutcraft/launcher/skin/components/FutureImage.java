package org.spoutcraft.launcher.skin.components;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.spoutcraft.launcher.skin.ImageCallback;

public class FutureImage extends Image implements ImageCallback{
	private final Future<BufferedImage> future;
	private final BufferedImage empty;
	private BufferedImage futureImage = null;

	/**
	 * Future image and empty image must be the same height and width
	 * 
	 * @param future
	 * @param empty
	 */
	public FutureImage(Future<BufferedImage> future, BufferedImage empty) {
		this.future = future;
		this.empty = empty;
	}

	public void done() {
		if (futureImage == null) {
			if (future.isDone()) {
				try {
					futureImage = future.get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		}
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
