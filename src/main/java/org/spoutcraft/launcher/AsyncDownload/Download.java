/*
 * This file is part of Spoutcraft Launcher (http://wiki.getspout.org/).
 * 
 * Spoutcraft Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spoutcraft Launcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.spoutcraft.launcher.AsyncDownload;

import java.io.*;
import java.net.*;
import java.nio.channels.ClosedByInterruptException;

/**
 * Downloads stuff asynchroniously.
 * In fact, it's a modified version of StackOverflow sample ;)
 */
public class Download implements Runnable {

	private static final int BUFFER = 1024;

	private URL url;
	private int size = -1;
	private int downloaded = 0;
	private String outPath;
	private DownloadListener listener;
	public boolean success = false;
	
	public Download(String url, String outPath) throws MalformedURLException {
		this.url = new URL(url);
		this.outPath = outPath;
	}

	public float getProgress() {
		return ((float) downloaded / size) * 100;
	}

	public static int maybeAvailable(final InputStream in, final byte[] buffer, long timeout)
			throws IOException, InterruptedException {

		final int[] dataReady = {0};
		final IOException[] maybeException = {null};
		final Thread reader = new Thread() {
			public void run() {
				try {
					dataReady[0] = in.read(buffer);
				} catch (ClosedByInterruptException e) {
					System.err.println("Reader interrupted.");
				} catch (IOException e) {
					maybeException[0] = e;
				}
			}
		};

		Thread interruptor = new Thread() {
			public void run() {
				reader.interrupt();
			}
		};

		reader.start();
		reader.join(timeout);
		if (reader.isAlive()) {
			interruptor.start();
			interruptor.join(5000);
			reader.join(5000);
		}

		if (maybeException[0] != null)
			throw maybeException[0];

		return dataReady[0];
	}
	
	public void run() {
		RandomAccessFile file = null;
		InputStream stream = null;

		try {
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setRequestProperty("Range", "bytes=" + downloaded + "-");

			connection.connect();

			if (connection.getResponseCode() / 100 != 2) {
				throw new IOException("Incorrect response code: " + connection.getResponseCode());
			}

			size = connection.getContentLength();
			if (listener != null) listener.stateChanged(outPath, 0);
			stream = connection.getInputStream();
			FileOutputStream out = new FileOutputStream(outPath);
			byte[] buffer = new byte[BUFFER];
			int length;
			while ((length = maybeAvailable(stream, buffer, 5000)) > 0) {
				out.write(buffer, 0, length);
				downloaded += length;
				stateChanged();
			}
			stream.close();
			out.close();
			if (listener != null) listener.stateChanged(outPath, 100);
			success = true;
			return;
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (file != null) {
				try {
					file.close();
				} catch (Exception ignored) {
				}
			}

			if (stream != null) {
				try {
					stream.close();
				} catch (Exception ignored) {
				}
			}
		}
	}

	private void stateChanged() {
		if (listener != null) listener.stateChanged(outPath, getProgress());
	}

	public void setListener(DownloadListener listener) {
		this.listener = listener;
	}
}