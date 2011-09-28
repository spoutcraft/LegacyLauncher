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
package org.spoutcraft.launcher.gui;

import java.awt.Desktop;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URI;

import javax.swing.JLabel;

public class HyperlinkJLabel extends JLabel implements MouseListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3801443131566852907L;
	private String url;
	public void mouseClicked(MouseEvent arg0) {
		if (!Desktop.isDesktopSupported()) return;
		Desktop desktop = Desktop.getDesktop();
		if (!desktop.isSupported( java.awt.Desktop.Action.BROWSE ) ) return;
		try {
			URI uri = new java.net.URI( url );
			desktop.browse( uri );
		}
		catch ( Exception e ) {
			System.err.println( e.getMessage() );
		}
	}
	
	public HyperlinkJLabel(String text, String url) {
		super(text);
		this.url = url;
		super.addMouseListener(this);
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent arg0) {
	}

	public void mouseReleased(MouseEvent arg0) {
	}

}
