package org.spoutcraft.launcher.GUI;

import java.applet.Applet;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import org.spoutcraft.launcher.Launcher;
import org.spoutcraft.launcher.MinecraftAppletEnglober;


public class LauncherFrame extends Frame implements WindowListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4524937541564722358L;
	private MinecraftAppletEnglober minecraft;
	
	public LauncherFrame() {
		super("Spoutcraft Custom Launcher");
		super.setVisible(true);
		this.setSize(new Dimension(870, 518));
		this.setResizable(true);
		this.addWindowListener(this);
		setIconImage(Toolkit.getDefaultToolkit().getImage(LoginForm.class.getResource("/org/spoutcraft/launcher/favicon.png")));
	}
	
	public void runGame(String user, String session, String downloadTicket, String mcpass) {
		
		this.setVisible(true);
		
		Applet applet = Launcher.getMinecraftApplet();

       minecraft = new MinecraftAppletEnglober(applet);
        /*minecraft.addParameter("userName", user);
        minecraft.addParameter("sessionId", session);
        minecraft.addParameter("downloadTicket", downloadTicket);
        minecraft.addParameter("stand-alone", "true");*/
        
        minecraft.addParameter("username", user);
        minecraft.addParameter("sessionid", session);
        minecraft.addParameter("downloadticket", downloadTicket);
        //minecraft.addParameter("stand-alone", "true");
        minecraft.addParameter("mppass", mcpass);
        
        /*
         this.launcher.customParameters.put("userName", values[2].trim());
      this.launcher.customParameters.put("latestVersion", values[0].trim());
      this.launcher.customParameters.put("downloadTicket", values[1].trim());
      this.launcher.customParameters.put("sessionId", values[3].trim()); 
         
         */
        
        applet.setStub(minecraft);
		
        this.add(minecraft);
        validate();
        
        minecraft.init();
        minecraft.setSize(getWidth(), getHeight());
              
        minecraft.start();
	}
	
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	
	public void windowClosing(WindowEvent e) {
	        System.exit(0);
     }

	
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
	}
}
