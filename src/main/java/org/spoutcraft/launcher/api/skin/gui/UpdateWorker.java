package org.spoutcraft.launcher.api.skin.gui;

import org.jdesktop.swingworker.SwingWorker;
import org.spoutcraft.launcher.api.Event;
import org.spoutcraft.launcher.api.Launcher;

public class UpdateWorker extends SwingWorker<Object, Object> {

	private final LoginFrame loginFrame;
	
	public UpdateWorker(LoginFrame loginFrame) {
		this.loginFrame = loginFrame;
	}
	
	@Override
	protected Object doInBackground() throws Exception {
		if (loginFrame.isMinecraftUpdateaAvailable()) {
			Launcher.getGameUpdater().updateMinecraft();
			Launcher.getGameUpdater().updateSpoutcraft();
		} else if (loginFrame.isSpoutcraftUpdateaAvailable()) {
			Launcher.getGameUpdater().updateSpoutcraft();
		}
		loginFrame.onRawEvent(Event.UPDATE_FINISHED);
		return null;
	}
	
	@Override
	protected void done() {
	}

}
