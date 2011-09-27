/*
 * This file is part of Spoutcraft Launcher (http://wiki.getspout.org/).
 * 
 * Spoutcraft Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spoutcraft Launcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.spoutcraft.launcher.GUI;

import java.applet.Applet;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JOptionPane;

import org.spoutcraft.launcher.Launcher;
import org.spoutcraft.launcher.MinecraftAppletEnglober;
import org.spoutcraft.launcher.MinecraftUtils;
import org.spoutcraft.launcher.PlatformUtils;

public class LauncherFrame extends Frame implements WindowListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4524937541564722358L;
	private MinecraftAppletEnglober minecraft;
	public static boolean errorInDownload = false;
	
	public LauncherFrame() {
		super("Spoutcraft");
		super.setVisible(true);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((dim.width-870)/2, (dim.height-518)/2);
		this.setSize(new Dimension(870, 518));
		this.setResizable(true);
		this.addWindowListener(this);
		setIconImage(Toolkit.getDefaultToolkit().getImage(LoginForm.class.getResource("/org/spoutcraft/launcher/favicon.png")));
	}
	
	public void runGame(String user, String session, String downloadTicket, String mcpass) {

		Applet applet = Launcher.getMinecraftApplet();
		if (applet == null || errorInDownload) {
			String message = "Failed to launch Spoutcraft!\nTry clearing the cache from the options menu.";
			JOptionPane.showMessageDialog(getParent(), message);
			this.setVisible(false);
			this.dispose();
			return;
		}

		minecraft = new MinecraftAppletEnglober(applet);

		minecraft.addParameter("username", user);
		minecraft.addParameter("sessionid", session);
		minecraft.addParameter("downloadticket", downloadTicket);
		minecraft.addParameter("mppass", mcpass);
		minecraft.addParameter("spoutcraftlauncher", "true");
		minecraft.addParameter("portable", PlatformUtils.isPortable() + "");
		if (MinecraftUtils.server != null) {
			minecraft.addParameter("server", MinecraftUtils.server);
			if (MinecraftUtils.port != null) {
				minecraft.addParameter("port", MinecraftUtils.port);
			}
		}
		
		applet.setStub(minecraft);

		this.add(minecraft);
		validate();

		minecraft.init();
		minecraft.setSize(getWidth(), getHeight());

		minecraft.start();
		
		this.setVisible(true);
		//System.gc();
	}

	public void windowActivated(WindowEvent e) {		
	}

	
	public void windowClosed(WindowEvent e) {
	}

	
	public void windowClosing(WindowEvent e) {
		if (LauncherFrame.this.minecraft != null) {
			LauncherFrame.this.minecraft.stop();
			LauncherFrame.this.minecraft.destroy();
		}
		try {
			Thread.sleep(10000L);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		System.out.println("Exiting Spoutcraft");
		System.exit(0);
	}

	
	public void windowDeactivated(WindowEvent e) {
		//System.gc();
	}

	
	public void windowDeiconified(WindowEvent e) {
	}

	
	public void windowIconified(WindowEvent e) {
	}

	
	public void windowOpened(WindowEvent e) {
	}
}
