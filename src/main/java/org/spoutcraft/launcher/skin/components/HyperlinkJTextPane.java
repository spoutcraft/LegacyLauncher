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

import org.spoutcraft.launcher.launcher.Launcher;
import org.spoutcraft.launcher.util.DesktopUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URI;

public class HyperlinkJTextPane extends JTextPane implements MouseListener {
    private static final long CLICK_DELAY = 250L;
    private static final long serialVersionUID = 1L;
    private String url;
    private long lastClick = System.currentTimeMillis();

    public HyperlinkJTextPane(String text, String url) {
        this.setText(text);
        this.url = url;
        super.addMouseListener(this);
        this.setHighlighter(null);
        this.setCaretColor(new Color(255, 255, 255, 0));
        this.setCursor(Launcher.getFrame().getCursor());
        setOpaque(false);
//		setBorder(new EmptyBorder(10, 10, 10, 10));
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(getBackground());
        Insets insets = getInsets();
        int x = insets.left;
        int y = insets.top;
        int width = getWidth() - (insets.left + insets.right);
        int height = getHeight() - (insets.top + insets.bottom);
        g.fillRect(x, y, width, height);
        super.paintComponent(g);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (lastClick + CLICK_DELAY > System.currentTimeMillis()) {
            System.out.println("click");
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

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    public static void browse(URI uri) {
        DesktopUtils.browse(uri);
    }
}
