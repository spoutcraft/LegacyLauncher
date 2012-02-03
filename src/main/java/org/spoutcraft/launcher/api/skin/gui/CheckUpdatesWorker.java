package org.spoutcraft.launcher.api.skin.gui;

import java.util.List;

import org.jdesktop.swingworker.SwingWorker;
import org.spoutcraft.launcher.api.Launcher;
import org.spoutcraft.launcher.api.events.FinishedUpdateCheckEvent;

public class CheckUpdatesWorker extends SwingWorker<Boolean, String> {
	
	private final LoginFrame loginFrame;
	private boolean mcUpdate = false, scUpdate = false;
	public CheckUpdatesWorker(LoginFrame loginFrame) {
		this.loginFrame = loginFrame;
	}
	
	protected Boolean doInBackground() throws Exception {
		publish("Checking for Minecraft Update...\n");
		try {
			mcUpdate = Launcher.getGameUpdater().isMinecraftUpdateAvailible();
		} catch (Exception e) {
			mcUpdate = false;
		}

		publish("Checking for Spoutcraft update...\n");
		try {
			scUpdate = mcUpdate || Launcher.getGameUpdater().isSpoutcraftUpdateAvailible();
		} catch (Exception e) {
			scUpdate = false;
		}
		return true;
	}

	protected void done() {		
		loginFrame.setSpoutcraftUpdateAvailable(scUpdate);
		loginFrame.setMinecraftUpdateAvailable(mcUpdate);
		loginFrame.onRawEvent(new FinishedUpdateCheckEvent());
		this.cancel(true);
	}

	protected void process(List<String> chunks) {
		loginFrame.getProgressBar().setString(chunks.get(0));
	}

}
