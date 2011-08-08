package org.spoutcraft.launcher;

import java.io.File;

import javax.swing.UIManager;

import org.spoutcraft.launcher.GUI.LoginForm;


public class Main {
	
	@SuppressWarnings("static-access")
	public Main() throws Exception {
		main(null);
	}
	
	public static void main(String[] args) throws Exception {		
		PlatformUtils.getWorkingDirectory().mkdir();
		new File(PlatformUtils.getWorkingDirectory(), "spoutcraft").mkdir();
		
		try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Ignore and continue with Metal
        }
		
		LoginForm login = new LoginForm();
		login.setVisible(true);
		login.onLoad();
	}
	
}
