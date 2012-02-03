package org.spoutcraft.launcher.api.skin.gui;

import org.jdesktop.swingworker.SwingWorker;
import org.spoutcraft.launcher.api.Launcher;
import org.spoutcraft.launcher.api.events.UpdateFinishedEvent;

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
		loginFrame.onRawEvent(new UpdateFinishedEvent());
		return null;
	}
	
	@Override
	protected void done() {
	}

}
