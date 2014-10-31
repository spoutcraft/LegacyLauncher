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

import net.technicpack.launchercore.exception.RestfulAPIException;
import net.technicpack.launchercore.restful.PlatformConstants;
import net.technicpack.launchercore.restful.RestObject;

public class LauncherInfo {

    public static LauncherBuild getLatestBuild(String stream) throws RestfulAPIException {
        LauncherBuild result = RestObject.getRestObject(LauncherBuild.class, PlatformConstants.API + "launcher/version/" + stream + "4");
        return result;
    }

    public static String getDownloadURL(LauncherBuild version, boolean isJar) throws RestfulAPIException {
        return isJar ? version.getJar() : version.getExe();
    }
}
