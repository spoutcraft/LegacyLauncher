/*
 * This file is part of Spoutcraft.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spoutcraft is licensed under the Spout License Version 1.
 *
 * Spoutcraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spoutcraft is distributed in the hope that it will be useful,
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
package org.spoutcraft.launcher.util;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ReadableByteChannel;
import org.apache.commons.io.IOUtils;

import org.spout.downpour.DefaultURLConnector;
import org.spout.downpour.DownpourCache;
import org.spoutcraft.launcher.exceptions.DownloadException;
import org.spoutcraft.launcher.exceptions.PermissionDeniedException;
import org.spoutcraft.launcher.rest.RestAPI;

public class Download implements Runnable, ProgressCallback {
	private URL url;
	private long size = -1;
	private String outPath;
	private DownloadListener listener;
	private Result result = Result.FAILURE;
	private File outFile = null;
	private Exception exception = null;;

	public Download(String url, String outPath) throws MalformedURLException {
		this.url = new URL(url);
		this.outPath = outPath;
	}

	public Exception getException() {
		return exception;
	}

	public void run(){
		ReadableByteChannel rbc = null;
		FileOutputStream fos = null;
		try {
			DownpourCache cache = RestAPI.getCache();
			InputStream in = cache.get(url, new DefaultURLConnector() {
				@Override
				public void setHeaders(URLConnection conn) {
					conn.setDoInput(true);
					conn.setDoOutput(false);
					System.setProperty("http.agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.162 Safari/535.19");
					conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.162 Safari/535.19");
					HttpURLConnection.setFollowRedirects(true);
					conn.setUseCaches(false);
					((HttpURLConnection)conn).setInstanceFollowRedirects(true);
					conn.setConnectTimeout(10000);
					conn.setReadTimeout(10000);
				}

				@Override
				public void onConnected(URLConnection conn) {
					size = conn.getContentLength();
				}
			}, false);

			outFile = new File(outPath);
			outFile.delete();

			rbc = new RBCWrapper(Channels.newChannel(in), size, this);
			fos = new FileOutputStream(outFile);

			progress(0);
			fos.getChannel().transferFrom(rbc, 0, size > 0 ? size : Integer.MAX_VALUE);
			in.close();
			rbc.close();
			if (size > 0) {
				if (size == outFile.length()) {
					result = Result.SUCCESS;
				}
			} else {
				result = Result.SUCCESS;
			}
		} catch (ClosedByInterruptException e) {
			result = Result.INTERRUPTED;
			exception = e;
		} catch (PermissionDeniedException e) {
			exception = e;
			result = Result.PERMISSION_DENIED;
		} catch (DownloadException e) {
			exception = e;
			result = Result.FAILURE;
		} catch (Exception e) {
			exception = e;
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(fos);
			IOUtils.closeQuietly(rbc);
		}
	}

	public void setListener(DownloadListener listener) {
		this.listener = listener;
	}

	public Result getResult() {
		return result;
	}

	public File getOutFile() {
		return outFile;
	}

	public enum Result {
		SUCCESS,
		FAILURE,
		PERMISSION_DENIED,
		INTERRUPTED,
	}

	public static class RBCWrapper implements ReadableByteChannel{
		private final ReadableByteChannel wrapped;
		private final long size;
		private final ProgressCallback callback;
		private long read;
		public RBCWrapper(ReadableByteChannel wrapped, long size, ProgressCallback callback) {
			this.wrapped = wrapped;
			this.size = size;
			this.callback = callback;
		}

		public boolean isOpen() {
			return wrapped.isOpen();
		}

		public void close() throws IOException {
			wrapped.close();
		}

		public int read(ByteBuffer dst) throws IOException {
			int num = wrapped.read(dst);
			if (num > 0) {
				read += num;
				callback.progress(read / (float)size);
			}
			return num;
		}
	}

	public void progress(float progress) {
		if (listener != null) listener.stateChanged(outPath, progress * 100F);
	}
}
