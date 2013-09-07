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

package org.spoutcraft.launcher.skin.components;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.spoutcraft.launcher.skin.TechnicLoginFrame;

public class BackgroundImage extends JLabel implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;

	private final TechnicLoginFrame frame;
	private int mouseX = 0, mouseY = 0;
	private AnimatedBackground background;

	public BackgroundImage(TechnicLoginFrame frame, int width, int height) {
		this.frame = frame;
		setVerticalAlignment(SwingConstants.CENTER);
		setHorizontalAlignment(SwingConstants.CENTER);
		setBounds(0, 0, width, height);

		setVerticalAlignment(SwingConstants.TOP);
		setHorizontalAlignment(SwingConstants.LEFT);
		setIcon(TechnicLoginFrame.getIcon("background.jpg", width, height));
		background = new AnimatedBackground(this);
		background.setIcon(TechnicLoginFrame.getIcon("background.jpg", width, height));
		background.setBounds(0, 0, width, height);

		this.add(background);
	}

	public synchronized void changeBackground(String name, Icon icon, boolean force) {
		background.changeIcon(name, icon, force);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		frame.setLocation(e.getXOnScreen() - mouseX, e.getYOnScreen() - mouseY);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
