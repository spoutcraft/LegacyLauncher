package org.spoutcraft.launcher.skin;

import org.spoutcraft.launcher.api.skin.JavaSkin;
import org.spoutcraft.launcher.api.skin.gui.LoginFrame;

public class DefaultSkin extends JavaSkin {

	private DefaultLoginFrame loginFrame = new DefaultLoginFrame(this);

	public void onDisable() {
	}

	public void onEnable() {
	}

	public LoginFrame getLoginFrame() {
		return loginFrame;
	}
}
