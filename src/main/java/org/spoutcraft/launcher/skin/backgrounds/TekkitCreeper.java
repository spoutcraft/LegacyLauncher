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
package org.spoutcraft.launcher.skin.backgrounds;

import org.spoutcraft.launcher.skin.components.AnimatedImage;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class TekkitCreeper extends AnimatedImage {
    private static final long serialVersionUID = 1;

    private final int x;
    private final int y;
    private static final int delay = 50;
    private final int distance = 30;
    private int modX = 0;
    private boolean xReverse = false;
    private int modY = 0;
    private boolean yReverse = false;


    public TekkitCreeper(int x, int y, Icon image) {
        super(image, delay);
        this.x = x;
        this.y = y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (modX == distance) {
            xReverse = true;
        }
        if (modX == 0) {
            xReverse = false;
        }
        if (modY == distance) {
            yReverse = true;
        }
        if (modY == 0) {
            yReverse = false;
        }

        if (xReverse) {
            modX--;
        } else {
            modX++;
        }

        if (yReverse) {
            modY--;
        } else {
            modY++;
        }

        int delayChange = 0;
        if (modX < distance / 2) {
            delayChange = distance - modX - (distance / 2);
        } else {
            delayChange = modX - (distance / 2);
        }
        getTimer().setDelay(delay + (delayChange * 10));

        this.setBounds(x + modX, y + modY, getWidth(), getHeight());
        this.repaint();
    }
}
