/*
 * This file is part of Spoutcraft.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
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
package org.spoutcraft.launcher.skin;

import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.util.Iterator;
import java.util.Random;
import javax.swing.JTextPane;

import org.jdesktop.swingworker.SwingWorker;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class AsyncRSSFeed extends SwingWorker<Object, Object> {
	private static final int MAX_ENTRIES = 2;
	private JTextPane editorPane;
	@SuppressWarnings("unused")
	private String username = null;
	private Random rand = new Random();

	public AsyncRSSFeed(JTextPane editorPane) {
		this.editorPane = editorPane;
	}

	public void setUser(String username) {
		this.username = username;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Object doInBackground(){
		try {
			editorPane.setText("Loading RSS feed...");
			URL url = new URL("http://updates.spout.org/rss");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(10000);
			conn.setDoInput(true);
			conn.setDoOutput(false);
			System.setProperty("http.agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.162 Safari/535.19");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.162 Safari/535.19");
			HttpURLConnection.setFollowRedirects(true);
			conn.setUseCaches(false);
			((HttpURLConnection)conn).setInstanceFollowRedirects(true);
			int response = ((HttpURLConnection)conn).getResponseCode();
			if (HttpURLConnection.HTTP_OK == response) {
				StringBuilder sb = new StringBuilder();
				SyndFeedInput input = new SyndFeedInput();
				SyndFeed feed;
				Iterator entries;
				feed = input.build(new XmlReader(conn));
				entries = feed.getEntries().iterator();

				int entryNum = 0;

				while (entries.hasNext()) {
					entryNum++;
					if (entryNum > MAX_ENTRIES) {
						break;
					}
					SyndEntry entry = (SyndEntry) entries.next();
					sb.append("<h1>").append(entry.getTitle()).append("</h1>").append("<br />").append(entry.getDescription().getValue()).append("<br /><br />");
				}

				if (sb.toString() != null) {
					editorPane.setText(sb.toString());
				} else {
					editorPane.setText(getErrorMessage());
				}

			} else {
				editorPane.setText(getErrorMessage());
			}
		} catch (SocketException e) {
			if (e.getMessage().equalsIgnoreCase("Permission denied: connect")) {
				editorPane.setText("Permission was denied - could not connect to RSS feed.");
			}
		} catch (Throwable t) {
			t.printStackTrace();
			editorPane.setText(t.toString());
		}
		editorPane.setVisible(true);
		return null;
	}

	private String getErrorMessage() {
		String[] errors = {"Oh dear, I'm out of tea and crumpets again. I'll have to go make some more.", "I'm sorry, were you looking for something here? I couldn't find it.", "This isn't the Tumblr news feed you are looking for. Move along now.", "What do you mean the website is down...Hey! What's that over there!", "Looks like the %mob%s got into the servers again...", "Oh Noes! Our Tumblr Feed is Down!"};
		return errors[rand.nextInt(errors.length)].replaceAll("%mob%", getRandomMob());
	}

	private String getRandomMob() {
		int mob = rand.nextInt(5);
		switch (mob) {
			case 0:
				return "spider";
			case 1:
				return "zombie";
			case 2:
				return "creeper";
			case 3:
				return "skeleton";
			case 4:
				return "ghast";
			default:
				return "";
		}
	}
}
