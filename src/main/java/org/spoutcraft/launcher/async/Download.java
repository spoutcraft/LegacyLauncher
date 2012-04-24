/*
 * This file is part of Spoutcraft Launcher (http://www.spout.org/).
 *
 * Spoutcraft Launcher is licensed under the SpoutDev License Version 1.
 *
 * Spoutcraft Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spoutcraft Launcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spoutcraft.launcher.async;

import java.io.*;
import java.net.*;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * Downloads stuff asynchroniously.
 * In fact, it's a modified version of StackOverflow sample ;)
 */
public class Download implements Runnable {
	private static final long TIMEOUT = 30000;

	private URL url;
	private long size = -1;
	private long downloaded = 0;
	private String outPath;
	private DownloadListener listener;
	private boolean success = false;
	private File outFile = null;
	public Download(String url, String outPath) throws MalformedURLException {
		this.url = new URL(url);
		this.outPath = outPath;
	}

	public float getProgress() {
		return ((float) downloaded / size) * 100;
	}

	public void run() {
		try {
			URLConnection conn = url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(false);
			System.setProperty("http.agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.162 Safari/535.19");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.162 Safari/535.19");
			HttpURLConnection.setFollowRedirects(true);
			conn.setUseCaches(false);
			((HttpURLConnection)conn).setInstanceFollowRedirects(true);
			int response = ((HttpURLConnection)conn).getResponseCode();
			System.out.println("Response for [" + url.getPath() + "] is [" + response + "]");
			InputStream in = getConnectionInputStream(conn);

			size = conn.getContentLength();
			outFile = new File(outPath);
			outFile.delete();

			final ReadableByteChannel rbc = Channels.newChannel(in);
			final FileOutputStream fos = new FileOutputStream(outFile);

			stateChanged();

			//Create a thread to monitor progress
			final Thread instance = Thread.currentThread();
			Thread progress = new Thread() {
				long last = System.currentTimeMillis();
				public void run() {
					while(!this.isInterrupted()) {
						long diff = outFile.length() - downloaded;
						downloaded = outFile.length();

						if (diff == 0) { //nothing downloaded
							if ((System.currentTimeMillis() - last) > TIMEOUT) { //waited too long
								if (listener != null) { //alert ui
									listener.stateChanged("Download Failed", getProgress());
								}
								try {
									rbc.close();
									instance.interrupt();
								} catch (IOException e) {
									e.printStackTrace();
								}
								return;
							}
						} else {
							last = System.currentTimeMillis();
						}

						stateChanged();
						try {
							sleep(100);
						} catch (InterruptedException ignore) {break;}
					}
				}
			};
			progress.start();

			fos.getChannel().transferFrom(rbc, 0, size > 0 ? size : Integer.MAX_VALUE);
			in.close();
			rbc.close();
			progress.interrupt();
			success = size > 0 ? (size == outFile.length()) : true;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected InputStream getConnectionInputStream(final URLConnection urlconnection) throws Exception {
		final InputStream[] is = new InputStream[1];

		for (int j = 0; (j < 3) && (is[0] == null); j++) {
			Thread stream = new Thread() {
				public void run() {
					try {
						is[0] = urlconnection.getInputStream();
					} catch (IOException ignore) { }
				}
			};
			stream.start();
			int iterationCount = 0;
			while ((is[0] == null) && (iterationCount++ < 5)) {
				try {
					stream.join(1000L);
				} catch (InterruptedException ignore) { }
			}
			if (is[0] != null) {
				continue;
			}
			try {
				stream.interrupt();
				stream.join();
			} catch (InterruptedException ignore) { }
		}

		if (is[0] == null) {
			throw new Exception("Unable to download file");
		}
		return new BufferedInputStream(is[0]);
	}

	private void stateChanged() {
		if (listener != null) {
			listener.stateChanged(outPath, getProgress());
		}
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
