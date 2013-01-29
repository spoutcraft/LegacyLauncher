package org.spoutcraft.launcher.technic;

import java.awt.Image;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;

import org.spoutcraft.launcher.exceptions.RestfulAPIException;
import org.spoutcraft.launcher.technic.rest.Modpack;
import org.spoutcraft.launcher.technic.rest.ModpackInfo;
import org.spoutcraft.launcher.technic.rest.TechnicRestAPI;

public class RestPack extends InstalledPack {
	private final ModpackInfo info;

	public RestPack(ModpackInfo info) throws IOException {
		super(info.getIcon(), info.getImg(), new ImageIcon(info.getBackground().getScaledInstance(880, 520, Image.SCALE_SMOOTH)));
		this.info = info;
		init();
	}

	public ModpackInfo getInfo() {
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
	public Modpack getModpack() {
		try {
			return TechnicRestAPI.getModpack(getInfo(), getBuild());
		} catch (RestfulAPIException e) {
			e.printStackTrace();
			return null;
		}
	}
}
