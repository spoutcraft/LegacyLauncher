/*
 * This file is part of Spoutcraft.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * Spoutcraft is licensed under the Spout License Version 1.
 *
 * Spoutcraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spoutcraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spoutcraft.launcher.yml;

import java.io.File;
import org.spoutcraft.launcher.util.Utils;

public enum Resources implements YAMLResource {
	Special ("http://get.spout.org/special.yml",
			new File(Utils.getWorkingDirectory(), "config" + File.separator + "special.yml"),
			null),

	VIP ("http://get.spout.org/vip.yml",
			new File(Utils.getWorkingDirectory(), "config" + File.separator + "vip.yml"),
			null),

	Assets ("http://get.spout.org/assets.yml",
			new File(Utils.getWorkingDirectory(), "config" + File.separator + "assets.yml"),
			null),
	;

	final BaseYAMLResource resource;
	private Resources(String url, File directory, ResourceAction action) {
		this.resource = new BaseYAMLResource(url, directory, action);
	}

	public YAMLProcessor getYAML() {
		return resource.getYAML();
	}

	public boolean updateYAML() {
		return resource.updateYAML();
	}
}