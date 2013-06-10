/*
 * This file is part of Technic Launcher.
 *
 * Copyright (c) 2013-2013, Technic <http://www.technicpack.net/>
 * Technic Launcher is licensed under the Spout License Version 1.
 *
 * Technic Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Technic Launcher is distributed in the hope that it will be useful,
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
package org.spoutcraft.launcher.technic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.spoutcraft.launcher.Settings;
import org.spoutcraft.launcher.rest.Modpack;
import org.spoutcraft.launcher.rest.pack.OfflineModpack;

public class OfflineInfo extends PackInfo {
	private final String name;
	private final String version;
	private boolean loading = true;
	
	public OfflineInfo(String name) {
		this.name = name;
		this.version = Settings.getModpackBuild(name);
		init();
	}

	@Override
	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	@Override
	public String getDisplayName() {
		return name;
	}

	@Override
	public String getRecommended() {
		return version;
	}

	public void setLoading(boolean loading) {
		this.loading = loading;
	}

	@Override
	public boolean isLoading() {
		return loading;
	}

	@Override
	public String getLatest() {
		return version;
	}

	@Override
	public List<String> getBuilds() {
		List<String> builds = new ArrayList<String>(1);
		builds.add(version);
		return builds;
	}

	@Override
	public Modpack getModpack() {
		File installed = new File(this.getBinDir(), "installed");
		if (installed.exists()) {
			return new OfflineModpack(getName(), getBuild());
		}

		return null;
	}
}
