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
package org.spoutcraft.launcher.util;

import java.util.List;

/**
 *
 * This is the 3rd version of SwingWorker (also known as
 * SwingWorker 3), an abstract class that you subclass to
 * perform GUI-related work in a dedicated thread.  For
 * instructions on using this class, see:
 *
 * http://java.sun.com/docs/books/tutorial/uiswing/misc/threads.html
 *
 * Note that the API changed slightly in the 3rd version:
 * You must now invoke start() on the SwingWorker after
 * creating it.
 *
 * @version $Revision: 971 $
 */
public abstract class SwingWorker<T, V> extends org.jdesktop.swingworker.SwingWorker<T, V> {
	/**
	 * This is a wrapper for get()
	 *
	 * @return T
	 */
	public T getValue() {
		try {
			return super.get();
		} catch( Exception ex) {
			return null;
		}
	}

	/**
	 * Receives data chunks from the {@code publish} method asynchronously on the
	 * <i>Event Dispatch Thread</i>.
	 * <p/>
	 * <p/>
	 * Please refer to the {@link #publish} method for more details.
	 *
	 * @param chunks intermediate results to process
	 * @see #publish
	 */
	protected void process(List<V> chunks) {
		super.process(chunks);
	}
}
