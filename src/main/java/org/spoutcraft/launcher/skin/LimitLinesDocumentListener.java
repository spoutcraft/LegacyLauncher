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

import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;

/**
 * From http://tips4java.wordpress.com/2008/10/15/limit-lines-in-document/
 *
 * @author Rob Camick
 */
public class LimitLinesDocumentListener implements DocumentListener {
	private int maximumLines;
	private boolean isRemoveFromStart;

	/**
	 * Specify the number of lines to be stored in the Document. Extra lines
	 * will be removed from the start or end of the Document, depending on
	 * the boolean value specified.
	 *
	 * @param maximumLines      number of lines
	 * @param isRemoveFromStart
	 */
	public LimitLinesDocumentListener(int maximumLines, boolean isRemoveFromStart) {
		setLimitLines(maximumLines);
		this.isRemoveFromStart = isRemoveFromStart;
	}

	/**
	 * Set the maximum number of lines to be stored in the Document
	 *
	 * @param maximumLines number of lines
	 */
	public void setLimitLines(int maximumLines) {
		if (maximumLines < 1) {
			String message = "Maximum lines must be greater than 0";
			throw new IllegalArgumentException(message);
		}

		this.maximumLines = maximumLines;
	}

	@Override
	public void insertUpdate(final DocumentEvent e) {
		// Changes to the Document can not be done within the listener
		// so we need to add the processing to the end of the EDT
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				removeLines(e);
			}
		});
	}

	private void removeLines(DocumentEvent e) {
		// The root Element of the Document will tell us the total number
		// of line in the Document.
		Document document = e.getDocument();
		Element root = document.getDefaultRootElement();

		while (root.getElementCount() > maximumLines) {
			if (isRemoveFromStart) {
				removeFromStart(document, root);
			} else {
				removeFromEnd(document, root);
			}
		}
	}

	private void removeFromEnd(Document document, Element root) {
		// We use start minus 1 to make sure we remove the newline
		// character of the previous line
		Element line = root.getElement(root.getElementCount() - 1);
		int start = line.getStartOffset();
		int end = line.getEndOffset();

		try {
			document.remove(start - 1, end - start);
		} catch (BadLocationException ble) {
			System.out.println(ble);
		}
	}

	private void removeFromStart(Document document, Element root) {
		Element line = root.getElement(0);
		int end = line.getEndOffset();

		try {
			document.remove(0, end);
		} catch (BadLocationException ble) {
			System.out.println(ble);
		}
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
	}
}
