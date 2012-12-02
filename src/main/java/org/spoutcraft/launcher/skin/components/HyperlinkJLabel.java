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
package org.spoutcraft.launcher.skin.components;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URI;

import org.spoutcraft.launcher.util.Compatibility;

public class HyperlinkJLabel extends TransparentJLabel implements MouseListener {
	private static final long CLICK_DELAY = 250L;
	private static final long serialVersionUID = 1L;
	private String url;
	private long lastClick = System.currentTimeMillis();
	public HyperlinkJLabel(String text, String url) {
		super(text);
		this.url = url;
		super.addMouseListener(this);
	}

	public void mouseClicked(MouseEvent e) {
		if (lastClick + CLICK_DELAY > System.currentTimeMillis()) {
			return;
		}
		lastClick = System.currentTimeMillis();
		try {
			URI uri = new java.net.URI(url);
			HyperlinkJLabel.browse(uri);
		} catch (Exception ex) {
			System.err.println("Unable to open browser to " + url);
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public static void browse(URI uri) {
		Compatibility.browse(uri);
	}
}
