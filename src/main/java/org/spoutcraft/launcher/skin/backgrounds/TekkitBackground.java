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
import org.spoutcraft.launcher.skin.components.EnhancedBackground;

public class TekkitBackground extends EnhancedBackground {
    private AnimatedImage tekkit;

    public TekkitBackground() {
        super("tekkitmain");
        tekkit = new TekkitCreeper(650, 100, ResourceUtils.getIcon("creeper.png", 107, 69));
        tekkit.setBounds(500, 100, 107, 69);
        tekkit.setVisible(false);
        this.add(tekkit);
    }


    @Override
    public void setVisible(boolean aFlag) {
        tekkit.setAnimating(aFlag);
        tekkit.setVisible(aFlag);
        super.setVisible(aFlag);
    }
}
