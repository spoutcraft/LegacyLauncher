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

package org.spoutcraft.launcher.yml;

import java.io.File;
import org.spoutcraft.launcher.util.Utils;

public enum Resources implements YAMLResource {
	Special ("http://get.spout.org/special.yml",
			new File(Utils.getLauncherDirectory(), "spoutcraft" + File.separator + "config" + File.separator + "special.yml"),
			null),

	VIP ("http://get.spout.org/vip.yml",
			new File(Utils.getLauncherDirectory(), "spoutcraft" + File.separator + "config" + File.separator + "vip.yml"),
			null),

	Assets ("http://get.spout.org/assets.yml",
			new File(Utils.getLauncherDirectory(), "spoutcraft" + File.separator + "config" + File.separator + "assets.yml"),
			null),
	;

	final BaseYAMLResource resource;
	private Resources(String url, File directory, ResourceAction action) {
		this.resource = new BaseYAMLResource(url, directory, action);
	}

	@Override
	public YAMLProcessor getYAML() {
		return resource.getYAML();
	}

	@Override
	public boolean updateYAML() {
		return resource.updateYAML();
	}
}