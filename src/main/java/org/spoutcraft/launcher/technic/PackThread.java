package org.spoutcraft.launcher.technic;

import java.util.logging.Level;

import org.spoutcraft.launcher.Settings;
import org.spoutcraft.launcher.api.Launcher;
import org.spoutcraft.launcher.exceptions.RestfulAPIException;
import org.spoutcraft.launcher.technic.rest.RestAPI;
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
		Launcher.getFrame().getSelector().redraw();
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
