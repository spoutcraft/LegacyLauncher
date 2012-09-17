package org.spoutcraft.launcher;

import org.spoutcraft.launcher.entrypoint.SpoutcraftLauncher;

public class Main {
	public static boolean old = false;

	public Main() {
		old = true;
		main(new String[0]);
		
	}

	public static void main(String[] args) {
		old = true;
		SpoutcraftLauncher.main(args);
	}
	
	public static boolean isOldLauncher() {
		return old;
	}
}
