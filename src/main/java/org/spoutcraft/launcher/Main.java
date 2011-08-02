package org.spoutcraft.launcher;

import java.io.File;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.spoutcraft.launcher.GUI.LauncherFrame;
import org.spoutcraft.launcher.GUI.LoginForm;


public class Main {

	@SuppressWarnings({ "unchecked", "rawtypes", "static-access" })
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
