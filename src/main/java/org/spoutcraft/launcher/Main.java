package org.spoutcraft.launcher;

import java.io.File;

import org.spoutcraft.launcher.GUI.LoginForm;


public class Main {

	@SuppressWarnings({ })
	public static void main(String[] args) throws Exception {
		PlatformUtils.getWorkingDirectory().mkdir();
		new File(PlatformUtils.getWorkingDirectory(), "spoutcraft");
		LoginForm login = new LoginForm();
		login.setVisible(true);	
	}
	
	
	public static void write(Object msg) {
		System.out.print(msg);
		System.out.append("\n");
	}
}
