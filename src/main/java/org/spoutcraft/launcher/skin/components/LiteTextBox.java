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

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class LiteTextBox extends JTextField implements FocusListener {
    private static final long serialVersionUID = 1L;
    protected final JLabel label;

    public LiteTextBox(JFrame parent, String label) {
        this.label = new JLabel(label);
        addFocusListener(this);
        parent.getContentPane().add(this.label);
        this.setBackground(new Color(220, 220, 220));
        this.setBorder(new LiteBorder(5, getBackground()));
        this.label.setForeground(Color.BLACK);
        this.setForeground(Color.BLACK);
        this.setCaretColor(Color.BLACK);
    }

    public LiteTextBox(JDialog parent, String label) {
        this.label = new JLabel(label);
        addFocusListener(this);
        parent.getContentPane().add(this.label);
        this.setBackground(new Color(220, 220, 220));
        this.setBorder(new LiteBorder(5, getBackground()));
        this.label.setForeground(Color.BLACK);
        this.setForeground(Color.BLACK);
        this.setCaretColor(Color.BLACK);
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        if (label != null) {
            label.setFont(font);
        }
    }

    @Override
    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, y, w, h);
        label.setBounds(x + 5, y + 3, w - 5, h - 5);
    }

    @Override
    public void focusGained(FocusEvent e) {
        label.setVisible(false);
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (getText().length() == 0) {
            label.setVisible(true);
        }
    }

    public void setLabelVisible(boolean visible) {
        label.setVisible(visible);
    }
}
