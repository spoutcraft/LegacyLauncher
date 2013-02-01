package org.spoutcraft.launcher.technic;

import java.awt.Image;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;

import org.spoutcraft.launcher.technic.rest.Modpack;
import org.spoutcraft.launcher.technic.rest.info.OfflineInfo;
import org.spoutcraft.launcher.technic.rest.pack.FallbackModpack;

public class InstalledOffline extends InstalledPack {
	private final OfflineInfo info;

	public InstalledOffline(OfflineInfo info) throws IOException {
		super(info.getIcon(), info.getLogo(), new ImageIcon(info.getBackground().getScaledInstance(880, 520, Image.SCALE_SMOOTH)));
		this.info = info;
		init();
	}

	@Override
	public String getLogoURL() {
		return "";
	}

	@Override
	public String getName() {
		return info.getName();
	}

	@Override
	public String getDisplayName() {
		return info.getName();
	}

	@Override
	public String getRecommended() {
		return info.getVersion();
	}

	@Override
	public String getLatest() {
		return info.getVersion();
	}

	@Override
	public List<String> getBuilds() {
		String[] builds = {info.getVersion()};
		return Arrays.asList(builds);
	}

	@Override
	public Modpack getModpack() {
		return new FallbackModpack(info.getName(), info.getVersion());
	}

}
