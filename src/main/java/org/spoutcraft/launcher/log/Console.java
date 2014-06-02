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

package org.spoutcraft.launcher.log;

import net.technicpack.launchercore.util.Settings;
import net.technicpack.launchercore.util.Utils;
import org.spoutcraft.launcher.skin.ConsoleFrame;

public class Console {
    private ConsoleFrame frame = null;
    private RotatingFileHandler handler = null;

    public Console(boolean isConsole) {
        if (isConsole || Settings.getShowConsole()) {
            setupConsole();
            Utils.getLogger().info("Console Mode Activated");
        }

        Thread logThread = new LogFlushThread(this);
        logThread.start();
    }

    public void setupConsole() {
        if (frame != null) {
            frame.dispose();
        }
        frame = new ConsoleFrame(2500, true);
        frame.setVisible(true);
    }

    public ConsoleFrame getFrame() {
        return frame;
    }

    public void setRotatingFileHandler(RotatingFileHandler handler) {
        this.handler = handler;
    }

    public RotatingFileHandler getHandler() {
        return handler;
    }

    public void destroyConsole() {
        if (frame != null) {
            frame.setVisible(false);
            frame.dispose();
        }
    }
}
