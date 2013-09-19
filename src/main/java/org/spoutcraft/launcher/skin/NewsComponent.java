/*
 * This file is part of Technic Launcher.
 * Copyright (C) 2013 Syndicate, LLC
 *
 * Technic Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Technic Launcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Technic Launcher.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.spoutcraft.launcher.skin;

import net.technicpack.launchercore.exception.RestfulAPIException;
import net.technicpack.launchercore.restful.PlatformConstants;
import net.technicpack.launchercore.restful.RestObject;
import net.technicpack.launchercore.restful.platform.Article;
import net.technicpack.launchercore.restful.platform.News;
import net.technicpack.launchercore.util.Utils;
import org.spoutcraft.launcher.skin.components.HyperlinkJTextPane;
import org.spoutcraft.launcher.skin.components.RoundedBox;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.List;
import java.util.logging.Level;

public class NewsComponent extends JComponent {
	private static final long serialVersionUID = 1L;

	public NewsComponent() {
		GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(LauncherFrame.getMinecraftFont(10));
	}

	public void loadArticles() {
		try {
			List<Article> articles = RestObject.getRestObject(News.class, PlatformConstants.NEWS).getNews();
			setupArticles(articles);
		} catch (RestfulAPIException e) {
			Utils.getLogger().log(Level.WARNING, "Unable to load news, hiding news section", e);
			this.setVisible(false);
			this.setEnabled(false);
		}
	}

	private void setupArticles(List<Article> articles) {
		Font articleFont = LauncherFrame.getMinecraftFont(10);
		int width = getWidth() - 16;
		int height = getHeight() / 2 - 16;

		for (int i = 0; i < 2; i++) {
			Article article = articles.get(i);
			String date = article.getDate();
			String title = article.getDisplayTitle();
			HyperlinkJTextPane link = new HyperlinkJTextPane(date + "\n" + title, article.getUrl());
			link.setFont(articleFont);
			link.setForeground(Color.WHITE);
			link.setBackground(new Color(255, 255, 255, 0));
			link.setBounds(8, 8 + ((height + 8) * i), width, height);
			this.add(link);
		}

		RoundedBox background = new RoundedBox(LauncherFrame.TRANSPARENT);
		background.setBounds(0, 0, getWidth(), getHeight());
		this.add(background);
		this.repaint();
	}
}
