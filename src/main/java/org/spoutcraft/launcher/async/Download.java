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
package org.spoutcraft.launcher.async;

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
	private long size = -1;
	private long downloaded = 0;
	private String outPath;
	private DownloadListener listener;
	private boolean success = false;
	private File outFile = null;
	private int retries = 3;
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
		run(false);
	}
	
	public void run(boolean resume) {
		RandomAccessFile file = null;
		InputStream stream = null;
		
		try {
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestProperty("Range", "bytes=0-");
			connection.connect();

			int tries = 5;
			boolean response = false;
			while (tries > 0) {
				if (connection.getResponseCode() / 100 != 2) {
					tries--;
					Thread.sleep(100);
				}
				else {
					response = true;
					break;
				}
			}
			if (!response) {
				throw new IOException("Incorrect response code: " + connection.getResponseCode());
			}
			
			//Don't reset the size, we already know it!
			if (!resume) {
				size = connection.getContentLength();
			}
			
			if (listener != null) listener.stateChanged(outPath, 0);
			stream = new BufferedInputStream(connection.getInputStream());
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outPath, resume));
			byte[] buffer = new byte[BUFFER];
			int length;
			boolean success = true;
			//Skip bytes in the stream to resume the download
			long toSkip = downloaded;
			if (resume && listener != null) listener.stateChanged("Download Failed, Attempting to Resume...", getProgress());
			int zeroSkips = 0;
			while (toSkip > 0) {
				long skipped = maybeAvailable(stream, buffer, 5000);
				if (skipped > 0) {
					toSkip -= skipped;
					zeroSkips = 0;
				}
				else {
					zeroSkips++;
				}
				if (zeroSkips > 3) {
					break; //failing!
				}
			}
			//Total restart
			if (toSkip > 0) {
				if (retries > 0) {
					listener.stateChanged("Download Failed, Restarting...", getProgress());
					retries--;
					run();
					return;
				}
				throw new IOException("Failed to complete download!");
			}
			stateChanged();
			while (true) {
				length = maybeAvailable(stream, buffer, 5000);
				if (length > 0) {
					out.write(buffer, 0, length);
					downloaded += length;
					stateChanged();
				}
				else {
					success = downloaded == size;
					break;
				}
			}
			stream.close();
			out.close();
			if (listener != null && success) {
				listener.stateChanged(outPath, 100);
			}
			outFile = new File(outPath);
			this.success = success;
			if (!success){
				//downloaded = outFile.length();
				run(true);
			}
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

	public boolean isSuccess() {
		return success;
	}

	public File getOutFile() {
		return outFile;
	}
}