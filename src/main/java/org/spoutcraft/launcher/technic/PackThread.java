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

import java.util.logging.Level;

import org.spoutcraft.launcher.Settings;
import org.spoutcraft.launcher.api.Launcher;
import org.spoutcraft.launcher.exceptions.RestfulAPIException;
import org.spoutcraft.launcher.rest.RestAPI;
import org.spoutcraft.launcher.util.Utils;

public class PackThread extends Thread {
	private final PackMap packs;
	private final String pack;

	public PackThread(PackMap packs, String pack) {
		super(pack + " Loading Thread");
		this.packs = packs;
		this.pack = pack;
	}

	@Override
	public void run() {
		PackInfo info = loadPack(packs, pack);
		packs.add(info);
		info.getLogo();
		info.getIcon();
		info.getBackground();
		Launcher.getFrame().getSelector().redraw(false);
	}

	private PackInfo loadPack(PackMap packs, String pack) {
		RestAPI rest = RestAPI.getDefault();
		boolean custom = Settings.isPackCustom(pack);

		PackInfo loaded = packs.get(pack);
		if (loaded != null && !loaded.isLoading()) {
			return loaded;
		}

		try {
			if (custom) {
				CustomInfo info = RestAPI.getCustomModpack(RestAPI.getCustomPackURL(pack));
				info.init();
				if (!info.hasMirror()) {
					packs.add(info);
					return info;
				}

				rest = new RestAPI(info.getMirrorURL());
			}

			RestInfo info = rest.getModpackInfo(pack);
			return info;

		} catch (RestfulAPIException e) {
			if (Utils.getStartupParameters().isDebugMode()) {
				Launcher.getLogger().log(Level.SEVERE, "Unable to load modpack " + pack + " from Technic Rest API", e);
			} else {
				Launcher.getLogger().log(Level.SEVERE, "Unable to load modpack " + pack + " from Technic Rest API");
			}
			PackInfo info = packs.get(pack);
			if (info instanceof OfflineInfo) {
				((OfflineInfo) info).setLoading(false);
			}
			return info;
		}
		
	}
}
