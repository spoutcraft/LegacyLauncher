package org.spoutcraft.launcher.technic;

import java.awt.Image;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;

import org.spoutcraft.launcher.exceptions.RestfulAPIException;
import org.spoutcraft.launcher.technic.rest.info.RestInfo;
import org.spoutcraft.launcher.technic.rest.pack.RestModpack;

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
	public List<String> getBuilds() {
		return Arrays.asList(info.getBuilds());
	}

	@Override
	public RestModpack getModpack() {
		try {
			return info.getRest().getModpack(info, getBuild());
		} catch (RestfulAPIException e) {
			e.printStackTrace();
		}
		return null;
	}
}
