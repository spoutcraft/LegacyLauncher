/*
 * This file is part of Launcher (http://www.spout.org/).
 *
 * Launcher is licensed under the SpoutDev License Version 1.
 *
 * Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Launcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spoutcraft.launcher;

import org.spoutcraft.launcher.api.GameUpdater;
import org.spoutcraft.launcher.api.util.DownloadListener;
import org.spoutcraft.launcher.yml.MinecraftYML;
import org.spoutcraft.launcher.yml.SpoutcraftYML;

public class SimpleGameUpdater extends GameUpdater {
	private long validationTime;
	private final UpdateThread updateThread;

	public SimpleGameUpdater() {
		super();
		updateThread = new UpdateThread();
		updateThread.setDaemon(true);
	}
	
	public void start() {
		updateThread.start();
	}
	
	public boolean isFinished() {
		return updateThread.isFinished();
	}
	
	public void setWaiting(boolean waiting) {
		updateThread.setWaiting(waiting);
	}

	public void setDownloadListener(DownloadListener listener) {
		super.setDownloadListener(listener);
		updateThread.setDownloadListener(listener);
	}

	public void clearVersionsInYMLs() {
		SpoutcraftYML.getSpoutcraftYML().setProperty("current", "");
		MinecraftYML.setInstalledVersion("");
	}

	public void setStartValidationTime(long validationTime) {
		this.validationTime = validationTime;
	}

	protected void validationFinished(boolean result) {
		long end = System.currentTimeMillis();
		System.out.println("------------------ Validation Finished  ------------------");
		System.out.println("Finished in " + (end - validationTime) + "ms");
		System.out.println("Result: " + result);
	}
}
