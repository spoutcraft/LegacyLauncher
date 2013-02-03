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

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;

import org.spoutcraft.launcher.exceptions.RestfulAPIException;
import org.spoutcraft.launcher.technic.rest.Modpack;
import org.spoutcraft.launcher.technic.rest.info.RestInfo;
import org.spoutcraft.launcher.technic.rest.pack.FallbackModpack;

public class InstalledRest extends InstalledPack {
	private final RestInfo info;

	public InstalledRest(RestInfo info) throws IOException {
		super(info.getIcon(), info.getLogo(), new ImageIcon(info.getBackground().getScaledInstance(880, 520, Image.SCALE_SMOOTH)));
		this.info = info;
		init();
	}

	public RestInfo getInfo() {
		return info;
	}

	@Override
	public String getName() {
		return info.getName();
	}

	@Override
	public String getDisplayName() {
		return info.getDisplayName();
	}

	@Override
	public String getRecommended() {
		return info.getRecommended();
	}

	@Override
	public String getLatest() {
		return info.getLatest();
	}

	@Override
	public String getLogoURL() {
		return info.getLogoURL();
	}

	@Override
	public List<String> getBuilds() {
		return Arrays.asList(info.getBuilds());
	}

	@Override
	public Modpack getModpack() {
		try {
			return info.getRest().getModpack(info, getBuild());
		} catch (RestfulAPIException e) {
			e.printStackTrace();

			File installed = new File(this.getBinDir(), "installed");
			if (installed.exists()) {
				return new FallbackModpack(getName(), getBuild());
			}

			return null;
		}
	}
}
