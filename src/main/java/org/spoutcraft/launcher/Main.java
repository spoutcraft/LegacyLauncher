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
package org.spoutcraft.launcher;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.UIManager;

import org.spoutcraft.launcher.gui.LoginForm;
import org.spoutcraft.launcher.gui.OptionDialog;
import org.spoutcraft.launcher.logs.SystemConsoleListener;

public class Main {
	
	static String[] args_temp;
	static File recursion = new File(PlatformUtils.getWorkingDirectory(), "rtemp");

	public Main() throws Exception {
		main(new String[0]);
	}

	public static void reboot(String memory) {
		try {
			String pathToJar = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
			ArrayList<String> params = new ArrayList<String>();
			params.add("javaw");
			params.add(memory);
			params.add("-classpath");
			params.add(pathToJar);
			params.add("org.spoutcraft.launcher.Main");
			for (String arg : args_temp) {
				params.add(arg);
			}
			ProcessBuilder pb = new ProcessBuilder(params);
			Process process = pb.start();
			if(process == null)
				throw new Exception("!");
			System.exit(0);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
		args_temp = args;
		boolean relaunch = false;
		try {
			if (!recursion.exists()) {
				relaunch = true;
			} else {
				recursion.delete();
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
		if (relaunch && OptionDialog.settings.checkProperty("memory")) {
			int mem = 1 << 8 + OptionDialog.settings.getPropertyInteger("memory");
			recursion.createNewFile();
			//reboot("-Xmx" + mem + "m");
		}
		
		PlatformUtils.getWorkingDirectory().mkdirs();

		new File(PlatformUtils.getWorkingDirectory(), "spoutcraft").mkdir();

		SystemConsoleListener listener = new SystemConsoleListener();

		listener.initialize();

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("[WARNING] Can't get system LnF: " + e);
		}

		LoginForm login = new LoginForm();

		switch (args.length) {
		case 4:
			if (Arrays.asList(args).contains("--portable") || new File("spoutcraft-portable").exists()) {
				PlatformUtils.setPortable(true);
			}

			login.doLogin(args[0], args[1]);

			MinecraftUtils.setServer(args[2]);
			break;

		case 3:
			if (Arrays.asList(args).contains("--portable") || new File("spoutcraft-portable").exists()) {
				PlatformUtils.setPortable(true);
			}

			login.doLogin(args[0], args[1]);

			MinecraftUtils.setServer(args[2]);
			break;
		case 2:
			if (Arrays.asList(args).contains("--portable") || new File("spoutcraft-portable").exists()) {
				PlatformUtils.setPortable(true);
			}

			login.doLogin(args[0], args[1]);

			break;
		default:
			if (args.length > 5) {
				if (Arrays.asList(args).contains("--portable") || new File("spoutcraft-portable").exists()) {
					PlatformUtils.setPortable(true);
				}

				MinecraftUtils.setServer(args[2]);
			}
		}

		login.setVisible(true);
	}

}
