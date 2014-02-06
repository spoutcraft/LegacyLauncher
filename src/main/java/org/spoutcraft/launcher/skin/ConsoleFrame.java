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

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.logging.Handler;
import java.util.logging.Logger;

/**
 * Console dialog for showing console messages.
 *
 * @author sk89q
 *         <p/>
 *         This code reused & relicensed as LGPL v 3 with permission.
 */
public class ConsoleFrame extends JFrame implements MouseListener {
	private static final long serialVersionUID = 1L;
	private static final Logger rootLogger = Logger.getLogger("launcher");
	private static String[] monospaceFontNames = {"Consolas", "DejaVu Sans Mono", "Bitstream Vera Sans Mono", "Lucida Console"};
	private final SimpleAttributeSet defaultAttributes = new SimpleAttributeSet();
	private final SimpleAttributeSet highlightedAttributes;
	private final SimpleAttributeSet errorAttributes;
	private final SimpleAttributeSet infoAttributes;
	private final SimpleAttributeSet debugAttributes;
	private Process trackProc;
	private Handler loggerHandler;
	private JTextComponent textComponent;
	private Document document;
	private int numLines;
	private boolean colorEnabled = false;

	/**
	 * Construct the frame.
	 *
	 * @param numLines     number of lines to show at a time
	 * @param colorEnabled true to enable a colored console
	 */
	public ConsoleFrame(int numLines, boolean colorEnabled) {
		this(numLines, colorEnabled, null, false);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	/**
	 * Construct the frame.
	 *
	 * @param numLines     number of lines to show at a time
	 * @param colorEnabled true to enable a colored console
	 * @param trackProc    process to track
	 * @param killProcess  true to kill the process on console close
	 */
	public ConsoleFrame(int numLines, boolean colorEnabled, final Process trackProc, final boolean killProcess) {
		super("Technic Launcher Console");
		this.numLines = numLines;
		this.colorEnabled = colorEnabled;
		this.trackProc = trackProc;

		this.highlightedAttributes = new SimpleAttributeSet();
		StyleConstants.setForeground(highlightedAttributes, Color.BLACK);
		StyleConstants.setBackground(highlightedAttributes, Color.YELLOW);

		this.errorAttributes = new SimpleAttributeSet();
		StyleConstants.setForeground(errorAttributes, new Color(200, 0, 0));
		this.infoAttributes = new SimpleAttributeSet();
		StyleConstants.setForeground(infoAttributes, new Color(200, 0, 0));
		this.debugAttributes = new SimpleAttributeSet();
		StyleConstants.setForeground(debugAttributes, Color.DARK_GRAY);

		setSize(new Dimension(650, 400));
		buildUI();

		this.setIconImage(Toolkit.getDefaultToolkit().getImage(LauncherFrame.icon));

		if (trackProc != null) {
			track();
		}

		addMouseListener(this);

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				if (trackProc != null && killProcess) {
					trackProc.destroy();
					if (loggerHandler != null) {
						rootLogger.removeHandler(loggerHandler);
					}
					event.getWindow().dispose();
				}
			}
		});
	}

	/**
	 * Track a process in a separate daemon thread.
	 */
	private void track() {
		final PrintWriter out = new PrintWriter(getOutputStream(Color.MAGENTA), true);
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					int code = trackProc.waitFor();
					out.println("Process ended with code " + code);
				} catch (InterruptedException e) {
					out.println("Process tracking interrupted!");
				}
			}
		});
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * Get an output stream using the give color.
	 *
	 * @param color color to use
	 * @return output stream
	 */
	public ConsoleOutputStream getOutputStream(Color color) {
		SimpleAttributeSet attributes = new SimpleAttributeSet();
		StyleConstants.setForeground(attributes, color);
		return getOutputStream(attributes);
	}

	/**
	 * Get an output stream with the given attribute set.
	 *
	 * @param attributes attributes
	 * @return output stream
	 */
	public ConsoleOutputStream getOutputStream(AttributeSet attributes) {
		return new ConsoleOutputStream(attributes);
	}

	/**
	 * Build the interface.
	 */
	private void buildUI() {
		if (colorEnabled) {
            this.textComponent = new JTextPane();
		} else {
			JTextArea text = new JTextArea();
			this.textComponent = text;
			text.setLineWrap(true);

		}
		textComponent.addMouseListener(this);
		textComponent.setFont(getMonospaceFont().deriveFont(14F));
		textComponent.setEditable(false);
		DefaultCaret caret = (DefaultCaret) textComponent.getCaret();
		caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		document = textComponent.getDocument();
		document.addDocumentListener(new LimitLinesDocumentListener(numLines, true));
		textComponent.setBackground(Color.BLACK);
		textComponent.setForeground(Color.WHITE);

		JScrollPane scrollText = new JScrollPane(textComponent);
		scrollText.setBorder(null);
		scrollText.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		add(scrollText, BorderLayout.CENTER);
	}

	/**
	 * Get a supported monospace font.
	 *
	 * @return font
	 */
	public static Font getMonospaceFont() {
		for (String fontName : monospaceFontNames) {
			Font font = Font.decode(fontName + "-11");
			if (!font.getFamily().equalsIgnoreCase("Dialog")) {
				return font;
			}
		}
		return new Font("Monospace", Font.PLAIN, 11);
	}

	/**
	 * Log a message.
	 *
	 * @param line line
	 */
	public void log(String line) {
		log(line, null);
	}

	/**
	 * Log a message given the {@link AttributeSet}.
	 *
	 * @param line       line
	 * @param attributes attribute set, or null for none
	 */
	public void log(String line, AttributeSet attributes) {
		if (colorEnabled) {
			if (line.startsWith("(!!)")) {
				attributes = highlightedAttributes;
			}
		}

		final String writeText = line.replace("\n\n", "\n");
		final AttributeSet writeAttributes = (attributes != null && colorEnabled) ? attributes : defaultAttributes;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					int offset = document.getLength();
					document.insertString(offset, writeText, writeAttributes);
					textComponent.setCaretPosition(document.getLength());
				} catch (BadLocationException ble) {
				} catch (NullPointerException npe) {
				}
			}
		});
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.isPopupTrigger()) {
			doPop(e);
		}
	}

	private void doPop(MouseEvent e) {
		ContextMenu menu = new ContextMenu();
		menu.show(e.getComponent(), e.getX(), e.getY());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger()) {
			doPop(e);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	/**
	 * Used to send console messages to the console.
	 */
	public class ConsoleOutputStream extends ByteArrayOutputStream {
		private AttributeSet attributes;

		private ConsoleOutputStream(AttributeSet attributes) {
			this.attributes = attributes;
		}

		@Override
		public void flush() {
			String data = toString();
			if (data.length() == 0) return;
			log(data, attributes);
			reset();
		}
	}

	private class ContextMenu extends JPopupMenu {
		private static final long serialVersionUID = 1L;
		JMenuItem copy;
		JMenuItem clear;

		public ContextMenu() {
			copy = new JMenuItem("Copy");
			add(copy);
			copy.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					textComponent.copy();
				}
			});

			clear = new JMenuItem("Clear");
			add(clear);
			clear.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					textComponent.setText("");
				}
			});
		}
	}
}
