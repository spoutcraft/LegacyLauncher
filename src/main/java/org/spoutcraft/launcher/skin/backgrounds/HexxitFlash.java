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

import net.technicpack.launchercore.util.ResourceUtils;
import org.spoutcraft.launcher.skin.components.AnimatedImage;

import java.awt.event.ActionEvent;

public class HexxitFlash extends AnimatedImage {
    private int counter = 0;

    public HexxitFlash() {
        super(ResourceUtils.getIcon("flash.jpg"), 75);
        this.setBounds(0, 0, 0, 0);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        counter++;

        if (counter == 41 || counter == 42 || counter == 47 || counter == 49) {
            this.setBounds(0, 0, 880, 520);
        } else {
            this.setBounds(0, 0, 0, 0);
        }

        if (counter >= 100) {
            counter = 0;
        }
    }
}
