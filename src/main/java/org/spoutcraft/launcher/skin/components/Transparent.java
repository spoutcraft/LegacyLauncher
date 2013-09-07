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

public interface Transparent {
	/**
	 * Gets the transparency of the label when no mouse is hovering over it
	 * 
	 * Values are between 0 and 1
	 * 
	 * @return transparency
	 */
	public float getTransparency();

	/**
	 * Sets the transparency of the label when no mouse is hovering over it
	 * 
	 * Values should be between 0 and 1
	 * 
	 * @param t transparency
	 */
	public void setTransparency(float t);

	/**
	 * Gets the transparency of the label when a mouse is hovering over it
	 * 
	 * Values are between 0 and 1
	 * 
	 * @return transparency
	 */
	public float getHoverTransparency();

	/**
	 * Sets the transparency of the label when a mouse is hovering over it
	 * 
	 * Values should be between 0 and 1
	 * 
	 * @param t transparency
	 */
	public void setHoverTransparency(float t);
}
