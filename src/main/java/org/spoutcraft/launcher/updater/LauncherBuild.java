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

package org.spoutcraft.launcher.updater;

import net.technicpack.launchercore.restful.RestObject;

public class LauncherBuild extends RestObject {
    private int build;
    private BuildUrl url;

    public int getLatestBuild() {
        return build;
    }
    public String getExe() { return url.getExe(); }
    public String getJar() { return url.getJar(); }

    public class BuildUrl {
        private String exe;
        private String jar;

        public String getExe() { return exe; }
        public String getJar() { return jar; }
    }
}

