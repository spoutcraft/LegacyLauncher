package org.spoutcraft.launcher.api.skin;

import java.awt.Frame;

public abstract class GameLauncher extends Frame {

	public GameLauncher(String title) {
		super(title);
	}
	
	public abstract int runGame(String user, String session, String downloadTicket, String mcpass);

}
