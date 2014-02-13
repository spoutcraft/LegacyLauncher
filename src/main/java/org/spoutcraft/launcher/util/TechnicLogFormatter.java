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

package org.spoutcraft.launcher.util;

import org.spoutcraft.launcher.entrypoint.SpoutcraftLauncher;
import org.spoutcraft.launcher.log.DateOutputFormatter;

import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class TechnicLogFormatter extends DateOutputFormatter {

    private String launcherBuild;

    public TechnicLogFormatter() {
        super(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"));
        launcherBuild = SpoutcraftLauncher.getLauncherBuild();
    }

    @Override
    public String format(LogRecord record) {
        return "[B#" + launcherBuild+"] "+super.format(record);
    }
}
