package org.spoutcraft.launcher.entrypoint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.spoutcraft.launcher.api.util.OperatingSystem;
import org.spoutcraft.launcher.api.util.Utils;

public class Mover {

	public static void main(String[] args) {
		main(args, false);
	}

	public static void main(String[] args, boolean exe) {
		File temp;
		if (exe) {
			temp = new File(Utils.getWorkingDirectory(), "temp.exe");
		} else {
			temp = new File(Utils.getWorkingDirectory(), "temp.jar");
		}
		File codeSource = new File(args[0]);
		codeSource.delete();
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(temp);
			fos = new FileOutputStream(codeSource);
			IOUtils.copy(fis, fos);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(fis);
			IOUtils.closeQuietly(fos);
		}
		
		ProcessBuilder processBuilder = new ProcessBuilder();
		ArrayList<String> commands = new ArrayList<String>();
		if (!exe) {
			if (OperatingSystem.getOS().isWindows()) {
				commands.add("javaw");
			} else {
				commands.add("java");
			}
			commands.add("-Xmx256m");
			commands.add("-cp");
			commands.add(codeSource.getAbsolutePath());
			commands.add(SpoutcraftLauncher.class.getName());
		} else {
			commands.add(temp.getAbsolutePath());
			commands.add("-Launcher");
		}
		commands.addAll(Arrays.asList(args));
		processBuilder.command(commands);

		try {
			processBuilder.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

}
