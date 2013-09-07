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

	public static int getLatestBuild(String stream) throws RestfulAPIException{
		LauncherBuild result = RestObject.getRestObject(LauncherBuild.class, PlatformConstants.API + "launcher/version/" + stream);
		return result.getLatestBuild();
	}

	public static String getDownloadURL(int version, boolean isJar) throws RestfulAPIException {
		String ext = isJar ? "jar" : "exe";

		String url = PlatformConstants.API + "launcher/url/" + version + "/" + ext;
		LauncherURL result = RestObject.getRestObject(LauncherURL.class, url);
		return result.getURL();
	}
}
