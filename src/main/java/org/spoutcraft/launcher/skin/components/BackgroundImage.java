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

import net.technicpack.launchercore.util.ImageUtils;
import net.technicpack.launchercore.util.ResourceUtils;
import org.spoutcraft.launcher.skin.LauncherFrame;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

public class BackgroundImage extends JLabel implements MouseListener, MouseMotionListener {
    private static final long serialVersionUID = 1L;
    private final LauncherFrame frame;
    private int mouseX = 0, mouseY = 0;
    private AnimatedBackground background;

    public BackgroundImage(LauncherFrame frame, int width, int height) {
        this.frame = frame;
        setVerticalAlignment(SwingConstants.CENTER);
        setHorizontalAlignment(SwingConstants.CENTER);
        setBounds(0, 0, width, height);

        setVerticalAlignment(SwingConstants.TOP);
        setHorizontalAlignment(SwingConstants.LEFT);
        setIcon(ResourceUtils.getIcon("background.jpg", width, height));
        background = new AnimatedBackground(this);
        background.setIcon(ResourceUtils.getIcon("background.jpg", width, height));
        background.setBounds(0, 0, width, height);

        this.add(background);
    }

    public synchronized void changeBackground(String name, BufferedImage image, boolean force) {
        background.changeIcon(name, new ImageIcon(ImageUtils.scaleImage(image, 880, 520)), force);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            mouseX = e.getX();
            mouseY = e.getY();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
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
    public void mouseDragged(MouseEvent e) {
        if ((e.getModifiersEx() & InputEvent.BUTTON1_DOWN_MASK) != 0) {
            frame.setLocation(e.getXOnScreen() - mouseX, e.getYOnScreen() - mouseY);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // TODO Auto-generated method stub

    }
}
