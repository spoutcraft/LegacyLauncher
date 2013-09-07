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

import java.awt.Graphics;
import javax.swing.JLabel;

public class TransparentJLabel extends JLabel implements Transparent{
	private static final long serialVersionUID = 1L;
	TransparentComponent transparency = new TransparentComponent(this);
	public TransparentJLabel() {
		super();
	}

	public TransparentJLabel(String text) {
		super(text);
	}

	@Override
	public float getTransparency() {
		return transparency.getTransparency();
	}

	@Override
	public void setTransparency(float t) {
		transparency.setTransparency(t);
	}

	@Override
	public float getHoverTransparency() {
		return transparency.getHoverTransparency();
	}

	@Override
	public void setHoverTransparency(float t) {
		transparency.setHoverTransparency(t);
	}

	@Override
	public void paint(Graphics g) {
		g = transparency.setup(g);
		super.paint(g);
		transparency.cleanup(g);
	}
}
